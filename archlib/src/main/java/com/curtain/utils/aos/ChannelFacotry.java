package com.curtain.utils.aos;

import android.content.Context;
import android.os.Environment;

import com.common.crypt.ByteCrypt;
import com.curtain.utils.io.FileUtils;
import com.curtain.arch.ChannelConfig;
import com.curtain.arch.MyLog;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * 产生渠道号
 * @author lijichuan
 * 
 */
public class ChannelFacotry {
	private static final String SRC_CID = ByteCrypt.getString("kingpin".getBytes());
	public static String ALLCATED_CID = "default";
	public static String CUSTOMIZED_CID ="default";
	private static String channel = null;
	//TODO 不同的应用可能会发生变化
	private static final String CHANNEL_FILE_NAME = ByteCrypt.getString("channel".getBytes());
	private static final String EXTERNAL_CHANNEL_SUB_DIR = ByteCrypt.getString("Juice".getBytes());
	private static final String EXTERNAL_CHANNEL_DIR = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ EXTERNAL_CHANNEL_SUB_DIR;

	public synchronized static String getChannel(Context context) {
		if (channel == null && !ALLCATED_CID.equals("") && !CUSTOMIZED_CID.equals("")) {
			channel = SRC_CID + "_" + ALLCATED_CID + "_" + CUSTOMIZED_CID;
		}
		if (channel == null) {
			File internalChannelFile = new File(context.getFilesDir(), CHANNEL_FILE_NAME);
			File externalChannelFile = new File(EXTERNAL_CHANNEL_DIR, CHANNEL_FILE_NAME);
			try {
				// 1.优先从内部文件中读取
				String tempChannel = readChannelFile(internalChannelFile);
				// 2. 应用目录中无效，则尝试从SD卡中导入
				if (invalidChannel(tempChannel)) {
					tempChannel = readChannelFile(externalChannelFile);
					if (validChannel(tempChannel)) {
						save2File(internalChannelFile, tempChannel);
					}
				}

				// 3. 从SD卡中导入失败，则重新生成
				if (invalidChannel(tempChannel)) {
					tempChannel = generateChannel();
					save2File(internalChannelFile, tempChannel);
					save2File(externalChannelFile, tempChannel);
				}

				channel = tempChannel;
			} catch (Exception e) {
				MyLog.w(e.getMessage());
				return null;
			}
		}
		return channel;
	}

	private static boolean validChannel(String channel) {
		if (channel == null || "".equals(channel.trim())) {
			return false;
		}

		return true;
	}

	private static boolean invalidChannel(String channel) {
		return !validChannel(channel);
	}

	private static String readChannelFile(File channelFile) throws IOException {
		if(channelFile == null || !channelFile.exists()) {
			return null;
		}
		RandomAccessFile f = new RandomAccessFile(channelFile, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static String generateChannel() {
		return ChannelConfig.CHANNEL;
	}

    private static void save2File(File f, String c) throws IOException{
        FileUtils.writeStringToFile(f,c);
    }
}