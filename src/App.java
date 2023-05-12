import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.*;
import javax.swing.JOptionPane;

public class App implements Watcher{

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 5000;
    private ArrayList<String> ZNODE_PATHS;
    private ZooKeeper zooKeeper;

    public static void main(String[] args) {
        try {           
            App app = new App();
            app.start();
            app.ZNODE_PATHS = new ArrayList<>();

            int Op = -1;
            while(Op != 0){
                String str = "";
                str += "Demonstração nós do zookeeper\n";
                str += "♦ 1 → Criar um nó\n";
                str += "♦ 2 → Listar nós\n";
                str += "♦ 3 → Ler um nó\n";
                str += "♦ 4 → Ler nós filhos\n";
                str += "♦ 5 → Ler todos os nós\n";
                str += "♦ 0 → Encerrar\n";
                str += "Digite uma opção: ";
                Op = Integer.parseInt(JOptionPane.showInputDialog(null, str));
                switch (Op) {
                    case 0:
                        break;
                    case 1:
                        // Criação de um znode com dados
                        String znode_path = JOptionPane.showInputDialog(null, "Criando um znode \nPath: ");
                        String message = JOptionPane.showInputDialog(null, "Mensagem: ");
                        app.ZNODE_PATHS.add(znode_path);
                        app.createZNode(znode_path, message);
                        break;
                    case 2:
                        JOptionPane.showMessageDialog(null, app.listarZNode());
                        break;
                    case 3:
                        // Leitura dos dados do znode
                        String znode_path_read = JOptionPane.showInputDialog(null, app.listarZNode() + "\nDigite um path: ");
                        JOptionPane.showMessageDialog(null, "Dados do znode lido: \n[Msg]→ " + app.readZNode(znode_path_read));
                        break;
                    case 4:
                        String znode_pai_path = JOptionPane.showInputDialog(null, app.listarZNode() + "\nDigite um path: "); 
                        app.readAllFilhos(znode_pai_path);
                        break;
                    case 5:
                        app.readAllZNode();
                        break;
                    default:
                        JOptionPane.showMessageDialog(null,"[AVISO]Opção inválida!");
                }

            }

            //Fecha a conexão com o servidor do Zookeeper
            app.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String listarZNode() {

       String str = "ZNodes criados";
        for (String path : this.ZNODE_PATHS) {
            str += "\n → "+path;
        }

        return str;
    }

    public void start() throws Exception {
        zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
        System.out.println("Conectando ao ZooKeeper...");
    }

    public void createZNode(String znode_path ,String data) throws KeeperException, InterruptedException {
        byte[] bytes = data.getBytes();
        String path = zooKeeper.create("/"+znode_path, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        JOptionPane.showMessageDialog(null,"Znode criado: " + path);
    }

    public String readZNode(String znode_path) throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData("/"+znode_path, this, null);
        String dataStr = new String(data);
        return dataStr;                
    }

    public void readAllZNode() throws KeeperException, InterruptedException {
        String str = "";
        for (String PATH : this.ZNODE_PATHS) {
            byte[] data = zooKeeper.getData("/"+PATH, this, null);
            String dataStr = new String(data);
            str += "\nDados do znode("+PATH+") lido: \n[Msg]→ " + dataStr;
        }
        JOptionPane.showMessageDialog(null, str);
    }

    public void readAllFilhos(String prPathPai) throws KeeperException, InterruptedException {
        List<String> mensagensNodes = zooKeeper.getChildren("/"+prPathPai, false);
        String str = "Nós do nó pai ["+prPathPai+"]";
        for (String node : mensagensNodes) {
           str += "\n  ♦ "+node+" \n      → [Msg] "+readZNode(prPathPai+"/"+node);
        }
        JOptionPane.showMessageDialog(null, str);
    }

    public void process(WatchedEvent event) {
         System.out.println("Evento recebido do ZooKeeper: " + event.getType());
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
        System.out.println("Conexão com o ZooKeeper encerrada.");
    }
}