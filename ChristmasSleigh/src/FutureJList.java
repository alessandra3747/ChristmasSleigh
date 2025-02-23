import javax.swing.*;

public class FutureJList extends JPanel implements Runnable{

    private static DefaultListModel<FutureInstance> listModel = new DefaultListModel<>();
    private static JList<FutureInstance> futureJList = new JList(listModel);;
    JScrollPane scrollPane;


    public FutureJList(){
        scrollPane = new JScrollPane(futureJList);

        futureJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        futureJList.setModel(listModel);

        this.add(futureJList);
    }

    @Override
    public void run() {

        while(!Thread.interrupted()){
            updateList();
            try{
                Thread.sleep(10);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }

    }

    public static void updateList(){
        listModel.removeAllElements();

        listModel.addElement(new FutureInstance("Snowflakes Future", SleighDrive.snowflakesFuture));
        listModel.addElement(new FutureInstance("Music Future", SleighDrive.musicFuture));
        listModel.addElement(new FutureInstance("RenderLines Future", SleighDrive.renderLinesFuture));
        listModel.addElement(new FutureInstance("Speedometer Future", SleighDrive.speedometerFuture));

        futureJList.setModel(listModel);


    }

}
