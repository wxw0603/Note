import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Timer {
    public static void main(String[] args) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        new Thread(()->{
            while(true){
                try{
                    tk.beep();
                    TimeUnit.SECONDS.sleep(2);
                    tk.beep();
                    TimeUnit.SECONDS.sleep(2);
                    tk.beep();
                    TimeUnit.MINUTES.sleep(30);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
