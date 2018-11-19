import java.util.Scanner;
/**
 *  Main
 *  This class makes use of the QueueProcess class to simulate a
 *  queue system with one line and multiple servers.
 *
 *  @author		A07104218	Salvador Orozco Villalever
 *  @author		A01328937	Luis Francisco Flores Romero
 *  @version	1.0
 *  @since		17.nov.2018
 */
public class Main{
    
    /**
     *  Main method
     *  Instantiate QueueProcess and start simulation.
     *
     *  @param    args    Input from input stream.
     *  @return   Nothing.
     */
    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        int x0, a, c, m, nc, ns, A, S;
        x0 = sc.nextInt();
        a = sc.nextInt();
        c = sc.nextInt();
        m = sc.nextInt();
        nc = sc.nextInt();
        ns = sc.nextInt();
        A = sc.nextInt();
        S = sc.nextInt();

        QueueProcess qp = new QueueProcess(x0, a, c, m, nc, ns, A, S);
        qp.runSimulation();
    }
}