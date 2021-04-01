import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IllegalArgumentException, IOException, ClassNotFoundException {
		DBParams.DBPath = args[0];
		DBManager dbmanager = DBManager.getInstance();
		dbmanager.init();
		Scanner s = new Scanner(System.in);
		String commande = "";
		boolean b = true;
		
			do {
					System.out.println(" ____________________________________________________________________________________________________");
					System.out.println("|                             Saisissez votre commande *EN MAJUSCULE                                 |");
					System.out.println("|           CREATEREL | INSERT | BATCHINSERT | SELECTALL | SELECTS  | SELECTC | RESET | EXIT         |");
					System.out.println("|____________________________________________________________________________________________________|");
				commande = s.nextLine();
				String[] comm = commande.split(" ");
				if (comm[0].equals("EXIT")) {
					dbmanager.finish();
					break;
				} else {
					try {
					dbmanager.ProcessCommand(commande);
					} catch (IllegalArgumentException i) {
						System.out.println("                        ***    Votre commande n'est pas bonne veuillez reessayez    ***");
					}
				}
			} while (b == true);
		
	}

}
