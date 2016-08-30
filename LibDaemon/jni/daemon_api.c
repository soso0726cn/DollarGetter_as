#include <fcntl.h>
#include <sys/stat.h>
#include <sys/inotify.h>

#include <stdio.h>
#include <dirent.h>
#include <unistd.h>

#include "com_slk_daemon_nativ_NativeDaemonAPI.h"
#include "log.h"
#include "constant.h"

void waitfor_self_observer(char* observer_file_path){
	int lockFileDescriptor = open(observer_file_path, O_RDONLY);
	if (lockFileDescriptor == -1){
		LOGE("Watched >>>>OBSERVER<<<< has been ready before watching...");
		return ;
	}

	void *p_buf = malloc(sizeof(struct inotify_event));
	if (p_buf == NULL){
		LOGE("malloc failed !!!");
		return;
	}
	int maskStrLength = 7 + 10 + 1;
	char *p_maskStr = malloc(maskStrLength);
	if (p_maskStr == NULL){
		free(p_buf);
		LOGE("malloc failed !!!");
		return;
	}
	int fileDescriptor = inotify_init();
	if (fileDescriptor < 0){
		free(p_buf);
		free(p_maskStr);
		LOGE("inotify_init failed !!!");
		return;
	}

	int watchDescriptor = inotify_add_watch(fileDescriptor, observer_file_path, IN_ALL_EVENTS);
	if (watchDescriptor < 0){
		free(p_buf);
		free(p_maskStr);
		LOGE("inotify_add_watch failed !!!");
		return;
	}


	while(1){
		size_t readBytes = read(fileDescriptor, p_buf, sizeof(struct inotify_event));
		if (4 == ((struct inotify_event *) p_buf)->mask){
			LOGE("Watched >>>>OBSERVER<<<< has been ready...");
			free(p_maskStr);
			free(p_buf);
			return;
		}
	}
}

void notify_daemon_observer(unsigned char is_persistent, char* observer_file_path){
	if(!is_persistent){
		int lockFileDescriptor = open(observer_file_path, O_RDONLY);
		while(lockFileDescriptor == -1){
			lockFileDescriptor = open(observer_file_path, O_RDONLY);
		}
	}
	remove(observer_file_path);
}

notify_and_waitfor(char *observer_self_path, char *observer_daemon_path){
	int observer_self_descriptor = open(observer_self_path, O_RDONLY);
	if (observer_self_descriptor == -1){
		observer_self_descriptor = open(observer_self_path, O_CREAT, S_IRUSR | S_IWUSR);
	}
	int observer_daemon_descriptor = open(observer_daemon_path, O_RDONLY);
	while (observer_daemon_descriptor == -1){
		usleep(1000);
		observer_daemon_descriptor = open(observer_daemon_path, O_RDONLY);
	}
	remove(observer_daemon_path);
	LOGE("Watched >>>>OBSERVER<<<< has been ready...");
}


/**
 *  Lock the file, this is block method.
 */
int lock_file(char* lock_file_path){
    LOGD("start try to lock file >> %s <<", lock_file_path);
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    if (lockFileDescriptor == -1){
        lockFileDescriptor = open(lock_file_path, O_CREAT, S_IRUSR);
    }
    int lockRet = flock(lockFileDescriptor, LOCK_EX);
    if (lockRet == -1){
        LOGE("lock file failed >> %s <<", lock_file_path);
        return 0;
    }else{
        LOGD("lock file success  >> %s <<", lock_file_path);
        return 1;
    }
}


JNIEXPORT void JNICALL Java_com_slk_daemon_nativ_NativeDaemonAPI_doDaemon21(JNIEnv *env, jobject jobj, jstring indicatorSelfPath, jstring indicatorDaemonPath, jstring observerSelfPath, jstring observerDaemonPath){
	if(indicatorSelfPath == NULL || indicatorDaemonPath == NULL || observerSelfPath == NULL || observerDaemonPath == NULL){
		LOGE("parameters cannot be NULL !");
		return ;
	}

	char* indicator_self_path = (char*)(*env)->GetStringUTFChars(env, indicatorSelfPath, 0);
	char* indicator_daemon_path = (char*)(*env)->GetStringUTFChars(env, indicatorDaemonPath, 0);
	char* observer_self_path = (char*)(*env)->GetStringUTFChars(env, observerSelfPath, 0);
	char* observer_daemon_path = (char*)(*env)->GetStringUTFChars(env, observerDaemonPath, 0);

	int lock_status = 0;
	int try_time = 0;
	while(try_time < 3 && !(lock_status = lock_file(indicator_self_path))){
		try_time++;
		LOGD("Persistent lock myself failed and try again as %d times", try_time);
		usleep(10000);
	}
	if(!lock_status){
		LOGE("Persistent lock myself failed and exit");
		return ;
	}

//	notify_daemon_observer(observer_daemon_path);
//	waitfor_self_observer(observer_self_path);
	notify_and_waitfor(observer_self_path, observer_daemon_path);

	lock_status = lock_file(indicator_daemon_path);
	if(lock_status){
		LOGE("Watch >>>>DAEMON<<<<< Daed !!");
		remove(observer_self_path);// it`s important ! to prevent from deadlock
		java_callback(env, jobj, DAEMON_CALLBACK_NAME);
	}

}




/**
 *  get the process pid by process name
 */
int find_pid_by_name(char *pid_name, int *pid_list){
    DIR *dir;
	struct dirent *next;
	int i = 0;
	pid_list[0] = 0;
	dir = opendir("/proc");
	if (!dir){
		return 0;
	}
	while ((next = readdir(dir)) != NULL){
		FILE *status;
		char proc_file_name[BUFFER_SIZE];
		char buffer[BUFFER_SIZE];
		char process_name[BUFFER_SIZE];

		if (strcmp(next->d_name, "..") == 0){
			continue;
		}
		if (!isdigit(*next->d_name)){
			continue;
		}
		sprintf(proc_file_name, "/proc/%s/cmdline", next->d_name);
		if (!(status = fopen(proc_file_name, "r"))){
			continue;
		}
		if (fgets(buffer, BUFFER_SIZE - 1, status) == NULL){
			fclose(status);
			continue;
		}
		fclose(status);
		sscanf(buffer, "%[^-]", process_name);
		if (strcmp(process_name, pid_name) == 0){
			pid_list[i ++] = atoi(next->d_name);
		}
	}
	if (pid_list){
    	pid_list[i] = 0;
    }
    closedir(dir);
    return i;
}

/**
 *  kill all process by name
 */
void kill_zombie_process(char* zombie_name){
    int pid_list[200];
    int total_num = find_pid_by_name(zombie_name, pid_list);
    LOGD("zombie process name is %s, and number is %d, killing...", zombie_name, total_num);
    int i;
    for (i = 0; i < total_num; i ++)    {
        int retval = 0;
        int daemon_pid = pid_list[i];
        if (daemon_pid > 1 && daemon_pid != getpid() && daemon_pid != getppid()){
            retval = kill(daemon_pid, SIGTERM);
            if (!retval){
                LOGD("kill zombie successfully, zombie`s pid = %d", daemon_pid);
            }else{
                LOGE("kill zombie failed, zombie`s pid = %d", daemon_pid);
            }
        }
    }
}

JNIEXPORT void JNICALL Java_com_slk_daemon_nativ_NativeDaemonAPI_doDaemon20(JNIEnv *env, jobject jobj, jstring pkgName, jstring svcName, jstring daemonPath){
	if(pkgName == NULL || svcName == NULL || daemonPath == NULL){
		LOGE("native doDaemon parameters cannot be NULL !");
		return ;
	}

	char *pkg_name = (char*)(*env)->GetStringUTFChars(env, pkgName, 0);
	char *svc_name = (char*)(*env)->GetStringUTFChars(env, svcName, 0);
	char *daemon_path = (char*)(*env)->GetStringUTFChars(env, daemonPath, 0);

	kill_zombie_process(NATIVE_DAEMON_NAME);

	int pipe_fd1[2];//order to watch child
	int pipe_fd2[2];//order to watch parent

	pid_t pid;
	char r_buf[100];
	int r_num;
	memset(r_buf, 0, sizeof(r_buf));
	if(pipe(pipe_fd1)<0){
		LOGE("pipe1 create error");
		return ;
	}
	if(pipe(pipe_fd2)<0){
		LOGE("pipe2 create error");
		return ;
	}

	char str_p1r[10];
	char str_p1w[10];
	char str_p2r[10];
	char str_p2w[10];

	sprintf(str_p1r,"%d",pipe_fd1[0]);
	sprintf(str_p1w,"%d",pipe_fd1[1]);
	sprintf(str_p2r,"%d",pipe_fd2[0]);
	sprintf(str_p2w,"%d",pipe_fd2[1]);


	if((pid=fork())==0){
		execlp(daemon_path,
				NATIVE_DAEMON_NAME,
				PARAM_PKG_NAME, pkg_name,
				PARAM_SVC_NAME, svc_name,
				PARAM_PIPE_1_READ, str_p1r,
				PARAM_PIPE_1_WRITE, str_p1w,
				PARAM_PIPE_2_READ, str_p2r,
				PARAM_PIPE_2_WRITE, str_p2w,
				(char *) NULL);
	}else if(pid>0){
		close(pipe_fd1[1]);
		close(pipe_fd2[0]);
		//wait for child
		r_num=read(pipe_fd1[0], r_buf, 100);
		LOGE("Watch >>>>CHILD<<<< Dead !!!");
		java_callback(env, jobj, DAEMON_CALLBACK_NAME);
	}
}