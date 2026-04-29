import java.util.*;
import java.util.concurrent.*;

/**
 * ACPs 智能体互联网系统 
 * 作者：Bowen
 * 包含：多线程、消息队列、状态机、工具类库、ACPs协议模拟
 */
public class ACPsAgentSystem {

    // 智能体状态
    enum AgentState {
        IDLE, WORKING, BUSY, SLEEPING, ERROR
    }

    // ==========================
    // 内部工具包
    // ==========================
    static class AgentTools {

        // 1. 时间格式化工具
        public static String formatTime(long timestamp) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(timestamp));
        }

        // 2. ACPs 协议加密工具（模拟）
        public static String encrypt(String content) {
            return "ACPs_ENCRYPTED::" + Base64.getEncoder().encodeToString(content.getBytes());
        }

        // 3. 解密工具
        public static String decrypt(String encrypted) {
            try {
                if (!encrypted.startsWith("ACPs_ENCRYPTED::")) return encrypted;
                String data = encrypted.replace("ACPs_ENCRYPTED::", "");
                return new String(Base64.getDecoder().decode(data));
            } catch (Exception e) {
                return "DECRYPT_FAILED";
            }
        }

        // 4. 报文校验工具
        public static boolean validateMessage(String content) {
            return content != null && !content.isBlank() && content.length() < 1000;
        }

        // 5. 生成全局唯一ID
        public static String generateUUID() {
            return UUID.randomUUID().toString();
        }

        // 6. 智能体性能监控
        public static void showPerformance(String agent, long start) {
            long cost = System.currentTimeMillis() - start;
            System.out.println("[性能监控] " + agent + " 执行耗时：" + cost + "ms");
        }

        // 7. 配置默认值工具
        public static <T> T getConfig(Map<String, T> config, String key, T defaultValue) {
            return config.getOrDefault(key, defaultValue);
        }

        // 8. 日志分级工具
        public static void log(String level, String agent, String msg) {
            System.out.printf("[%s] [%s] %s%n", level, agent, msg);
        }
    }

    // ==========================
    // 消息结构体
    // ==========================
    static class AgentMessage {
        String msgId;
        String from;
        String to;
        String content;
        String encryptedContent;
        long timestamp;

        public AgentMessage(String from, String to, String content) {
            this.msgId = AgentTools.generateUUID();
            this.from = from;
            this.to = to;
            this.content = content;
            this.encryptedContent = AgentTools.encrypt(content);
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("[%s] %s → %s | %s",
                    AgentTools.formatTime(timestamp), from, to, content);
        }
    }

    // ==========================
    // 智能体核心类
    // ==========================
    static class ACPsAgent implements Runnable {
        private final String agentId;
        private final String name;
        private AgentState state;
        private final BlockingQueue<AgentMessage> messageQueue;
        private final Map<String, Object> metadata;
        private boolean isRunning;

        public ACPsAgent(String name) {
            this.agentId = AgentTools.generateUUID();
            this.name = name;
            this.state = AgentState.IDLE;
            this.messageQueue = new LinkedBlockingQueue<>();
            this.metadata = new HashMap<>();
            this.isRunning = true;

            metadata.put("creator", "Bowen");
            metadata.put("protocol", "ACPs/2.0");
            metadata.put("version", "2.1");
            metadata.put("maxRetry", 3);
        }

        // 接收消息
        public void receiveMessage(AgentMessage msg) {
            if (!AgentTools.validateMessage(msg.content)) {
                AgentTools.log("ERROR", name, "消息格式非法");
                return;
            }
            messageQueue.offer(msg);
            AgentTools.log("INFO", name, "收到新消息，进入忙碌状态");
            setState(AgentState.BUSY);
        }

        // 发送消息
        public void sendMessage(ACPsAgent target, String content) {
            long start = System.currentTimeMillis();
            AgentMessage msg = new AgentMessage(this.name, target.name, content);
            target.receiveMessage(msg);
            System.out.println("[发送] " + msg);
            AgentTools.showPerformance(name + " sendMessage", start);
        }

        // 消息处理
        private void processMessage(AgentMessage msg) {
            long start = System.currentTimeMillis();
            AgentTools.log("PROCESS", name, "开始处理消息");

            String raw = AgentTools.decrypt(msg.encryptedContent);
            System.out.println("[解密结果] " + raw);

            if (msg.content.contains("Hello")) {
                System.out.println("[回复] " + name + " → " + msg.from + "：连接成功！\n");
            } else if (msg.content.contains("status")) {
                System.out.println("[状态] 当前状态：" + state + "\n");
            } else if (msg.content.contains("task")) {
                System.out.println("[任务] ACPs 协同任务执行中...\n");
            } else {
                System.out.println("[完成] 消息已处理\n");
            }

            AgentTools.showPerformance(name + " process", start);
            setState(AgentState.IDLE);
        }

        public void setState(AgentState state) {
            this.state = state;
        }

        @Override
        public void run() {
            AgentTools.log("SYSTEM", name, "智能体已启动 | ID：" + agentId);
            while (isRunning) {
                try {
                    AgentMessage msg = messageQueue.poll(500, TimeUnit.MILLISECONDS);
                    if (msg != null) {
                        processMessage(msg);
                    } else {
                        if (state != AgentState.SLEEPING) setState(AgentState.IDLE);
                    }
                } catch (InterruptedException e) {
                    AgentTools.log("ERROR", name, "线程中断");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        public void shutdown() {
            isRunning = false;
            AgentTools.log("SYSTEM", name, "安全关闭完成");
        }
    }

    // ==========================
    // 系统入口
    // ==========================
    public static void main(String[] args) throws InterruptedException {
        System.out.println("===== ACPs 智能体互联网系统（高级完整版）=====\n");

        ACPsAgent master = new ACPsAgent("Master-Agent");
        ACPsAgent worker = new ACPsAgent("Worker-Agent");

        new Thread(master).start();
        new Thread(worker).start();

        Thread.sleep(1000);
        master.sendMessage(worker, "Hello ACPs Network");

        Thread.sleep(1000);
        worker.sendMessage(master, "status check");

        Thread.sleep(1000);
        master.sendMessage(worker, "execute ACPs sync task");

        Thread.sleep(2000);
        master.shutdown();
        worker.shutdown();

        System.out.println("\n===== 系统运行结束 =====");
    }
}
