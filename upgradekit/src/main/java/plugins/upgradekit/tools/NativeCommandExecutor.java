/**
 * NativeCommandExecutor.java.java
 * @author FengMy
 * @since 2015年12月9日
 */
package plugins.upgradekit.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import plugins.installation.logs.MessageWriter;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年12月9日
 */
public class NativeCommandExecutor {
	private static final Logger logger = LoggerFactory.getLogger(NativeCommandExecutor.class);

	public static void main(String[] args) {
		MessageWriter writer = new MessageWriter() {
			PrintWriter pw = new PrintWriter(System.out);
			@Override
			public void write(String message) {
				pw.write(message + "\n");
				pw.flush();
			}
			@Override
			public boolean isAvailable() {
				return true;
			}
		};
		executeNativeCommand(writer, "gbk", "java -version", new String[]{},null,300);
	}
	
	/**
	 * 执行本地命令
	 * @param writer
	 * @param charset
	 * @param command
	 * @param params
	 * @param timeout 超时时间，单位为毫秒
	 */
	public static void executeNativeCommand(MessageWriter writer,String charset,String command,String[] params,File dir,int timeout){
		Process process = null;
		try{
			if(dir == null || !dir.exists() || dir.isFile()){
				dir = null;
			}
			logger.debug("cmd:{},params:{},dir",command,params,dir);
			process = Runtime.getRuntime().exec(command, params, dir);
			BufferedReader ebr = new BufferedReader(new InputStreamReader(process.getErrorStream(),charset));
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(),charset));
			
			CountDownLatch latch = new CountDownLatch(2);
			ExecutorService executor = Executors.newFixedThreadPool(2);
			executor.execute(new BufferedReadThread(ebr, writer,latch));
			executor.execute(new BufferedReadThread(br, writer,latch));
			latch.await(timeout, TimeUnit.MILLISECONDS);
			process.destroy();
			executor.shutdownNow();
		}catch(Exception e){
			logger.error("本地命令执行异常",e);
			if(writer != null){
				writer.write(e.getMessage());
			}
		}finally{
			if(process != null){
				process.destroy();
			}
		}
	}
	
	private static class BufferedReadThread extends Thread{
		private MessageWriter writer;
		private BufferedReader br;
		private CountDownLatch latch;
		BufferedReadThread(BufferedReader br,MessageWriter writer, CountDownLatch latch){
			this.br = br;
			this.writer = writer;
			this.latch = latch;
		}
		@Override
		public void run() {
			try{
				String line = null;
				while((line = br.readLine()) != null){
					logger.debug(line);
					writer.write(line);
				}
			}catch(Exception e){
				logger.error("读失败", e);
			}finally{
				latch.countDown();
			}
		}
	}
}
