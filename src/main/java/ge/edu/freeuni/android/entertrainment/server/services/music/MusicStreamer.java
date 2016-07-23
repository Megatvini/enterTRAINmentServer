package ge.edu.freeuni.android.entertrainment.server.services.music;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "ge.edu.freeuni.android.entertrainment.server.services.music.MusicStreamer", urlPatterns = "song")
public class MusicStreamer extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Content-Type","audio/mpeg");
        response.setHeader("Accept-Ranges","bytes");


        ServletOutputStream outputStream = response.getOutputStream();
        SharedData sharedData = SharedData.getInstance();
        int identifier = -1;
        while (true){
            sharedData.lock.lock();
            while (identifier == sharedData.getIdentifier()) {
                try {
                    System.out.println("waiting");
                    sharedData.notEmpty.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("out waiting");
            sharedData.lock.unlock();
            identifier = sharedData.getIdentifier();
            byte[] data = sharedData.getData();
            outputStream.write(data);
            System.out.println("writing");
            System.out.println(data.length);
        }


    }




}
