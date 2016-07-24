package ge.edu.freeuni.android.entertrainment.server.services.music;


public class DataUpdater extends Thread {

    private SharedData sharedData = SharedData.getInstance();

    @Override
    public void run() {
        while (true){
            try {
                sleep(1000);
                sharedData.updateData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
