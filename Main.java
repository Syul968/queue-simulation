import java.util.Scanner;

public class Main{

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