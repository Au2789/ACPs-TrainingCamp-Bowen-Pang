import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/**
 * ACPs智能体互联网实训营
 * 功能：简易智能体基础模型 + 消息通信模拟
 * 作者：Bowen
 */
public class AgentBasic {
    // 智能体唯一标识
    private final String agentId;
    private final String agentName;
    private final Map<String, Object> agentAttributes;

    // 构造方法：初始化智能体
    public AgentBasic(String agentName) {
        this.agentId = UUID.randomUUID().toString();
        this.agentName = agentName;
        this.agentAttributes = new HashMap<>();
    }

    // 设置属性
    public void setAttribute(String key, Object value) {
        agentAttributes.put(key, value);
    }

    // 获取属性
    public Object getAttribute(String key) {
        return agentAttributes.get(key);
    }

    // 发送消息
    public String sendMessage(String targetAgentId, String content) {
        return String.format(
            "[Agent:%s] 向 [%s] 发送消息：%s",
            this.agentId, targetAgentId, content
        );
    }

    // 展示智能体信息
    public void showInfo() {
        System.out.println("===== 智能体信息 =====");
        System.out.println("ID: " + agentId);
        System.out.println("名称: " + agentName);
        System.out.println("属性: " + agentAttributes);
        System.out.println("=====================\n");
    }

    // 主方法：运行演示
    public static void main(String[] args) {
        try {
            // 创建两个智能体
            AgentBasic agent1 = new AgentBasic("ACPs-Agent-01");
            AgentBasic agent2 = new AgentBasic("ACPs-Agent-02");

            // 设置属性
            agent1.setAttribute("version", "1.0");
            agent1.setAttribute("owner", "Bowen");
            agent2.setAttribute("protocol", "ACPs");

            // 展示信息
            agent1.showInfo();
            agent2.showInfo();

            // 模拟消息通信
            String msg = agent1.sendMessage(agent2.agentId, "Hello ACPs! Let's interconnect.");
            System.out.println(msg);

        } catch (Exception e) {
            System.err.println("运行异常：" + e.getMessage());
        }
    }
}
