import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;

public class DBManager {
	private static DBManager INSTANCE;
	private List<String> types = new ArrayList<String>();
	FileManager filemanager = FileManager.getInstance();
	BufferManager buffermanager = BufferManager.getInstance();

	private DBManager() {

	}

	public static synchronized DBManager getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new DBManager();
			return INSTANCE;
		} else
			return INSTANCE;
	}


	/**
	 *
	 * 
	 *  @throws IOException
	 *  @throws ClassNotFoundException
	 */
	public void init() throws IOException, ClassNotFoundException {
		DBInfo.getInstance().init();
		filemanager.init();
	}


	/**
	 *
	 * 
	 *  @throws IOException
	 */
	public void finish() throws IOException {
		DBInfo.getInstance().finish();
		buffermanager.flushAll();
	}


	/**
	 *	fonction qui recupere la saisie et la passe en dans un menu 
	 *  @param saisie 
	 *  @throws IOException
	 */
	public void ProcessCommand(String saisie) throws IOException {
		String[] tab = saisie.split(" ");
		int nbrcolonnes=0;
		List<String> liste = new ArrayList<String>();
		 List<String> nomcol=new ArrayList<String>();

		// Menu
		switch (tab[0]) {
			case "CREATEREL":
				for (int i = 2; i < tab.length; i++) {
					String[] tab1 =tab[i].split(":");
					for(int j = 0 ; j<tab1.length ; j++) {
						nomcol.add(tab1[j]);
						liste.add(tab1[j+1]);
						j++;
						nbrcolonnes++;
					}
				}
				//appel fonction
				CreateRelation(tab[1], nbrcolonnes, liste,nomcol);
				break;


			case "INSERT":
				insert(tab);
				break;

			case "BATCHINSERT":
				insertfichier(tab);
				break;

			case "RESET":
				reset();
				break;

			case "SELECTALL":
				selectall(tab[2]);
				break;

			case "SELECTS":
				selects(tab);
				break;

			case "SELECTC":
				selectc(tab);
				break;
		
			default:
				throw new IllegalArgumentException("Unexpected value: " + tab[0]);
		}
	}


	/**
	 *	fonction pour creer une relation
	 *  @param nom
	 *  @param nbcol
	 *  @param types
	 *  @param nomcol
	 *  @throws IOException
	 */
	public void CreateRelation(String nom, int nbcol, List<String> types,List<String> nomcol) throws IOException {
		int recordSize = 0;

		for (int i = 0; i < nbcol; i++) 
		{
			
			if (types.get(i).equals("int"))
			 {
				recordSize += 4;
			} 
			else if (types.get(i).equals("float"))
			 {
				recordSize += 4;
			} 
			else if (types.get(i).substring(0, 6).equals("string")) 
			{
				int valeur = Integer.parseInt(types.get(i).substring(6));
				recordSize += 2 * valeur;
			}
		}
		int slotCount = DBParams.pageSize / (recordSize + 1);
		int fileIdx = DBInfo.getInstance().getCompteur();

		RelationInfo relinfo = new RelationInfo(nom, nbcol, types,nomcol, fileIdx, recordSize, slotCount);
		DBInfo.getInstance().addRelation(relinfo);
		filemanager.CreateRelationFile(relinfo);
		System.out.println("Relation cree");
	}


	/**	fonction pour tous supprimer
	 *  @throws IOException
	 */
	public void reset() throws IOException {
		File repertoire = new File(DBParams.DBPath);
		BufferManager.getInstance().reset();
		filemanager.reset();
		DBInfo.getInstance().reset();
		File[] fichiers = repertoire.listFiles();
		for (int i = 0; i < fichiers.length; i++) {
			fichiers[i].delete();
		}
	}


	/**	Inserer des donnée 1 par 1
	 *  @param commandeI
	 *  @throws IOException
	 */
	public void insert(String[] commandeI) throws IOException {
		Record record = new Record();
		commandeI[4] = commandeI[4].replace("(", "");
		commandeI[4] = commandeI[4].replace(")", "");
		String[] tab = commandeI[4].split(",");
		
		for (int i = 0; i < tab.length; i++) 
		{
			record.setValues(tab[i]);
		}
		filemanager.InsertRecordInRelation(record, commandeI[2]);
		System.out.println("Record insere");


	}
	/**	Inserer des avec un fichier
	*  @param commandeIF
	*  @throws IOException
	*/
	public void insertfichier(String[] commandeIF) throws IOException {
		String nomRelation = commandeIF[2];
		
		File fichierCsv = new File(DBParams.DBPath + "/../" + commandeIF[5]);

		List<String> lignes = new ArrayList<String>();
		FileReader fr = new FileReader(fichierCsv);
		BufferedReader buffer = new BufferedReader(fr);

		String ligne;

		while ((ligne = buffer.readLine()) != null) {
			Record record = new Record();
			String[] tmp = ligne.split(",");
			for (String val : tmp) {
				record.setValues(val);
			}

			filemanager.InsertRecordInRelation(record, nomRelation);
		}
		
		buffer.close();
		fr.close();
		System.out.println("Record insere");
	}


	/**	selectionner toutes les donnée d'une relation
	*  @param nomRelation
	*  @throws IOException
	*/
	public void selectall(String nomRelation) throws IOException {
		ArrayList<Record> listerec = new ArrayList<Record>();
		listerec = filemanager.SelectAllFromRelation(nomRelation);
		
		for (int i = 0; i < listerec.size(); i++) {
			for (int j = 0; j < listerec.get(i).getValues().size(); j++) {
				if (j==listerec.get(i).getValues().size()-1){
					System.out.print(listerec.get(i).getValues().get(j).toString() + ".");
				}else {
					System.out.print(listerec.get(i).getValues().get(j).toString() + " ; ");
				}	
			}
			System.out.println();
		}
		System.out.println("Total records="+listerec.size()+",");
	}


	/**	verifier si un operateur fait parti d'une condition 
	 *  @param condition
	 *	@param operateur
	 *  @throws IOException
	 */
	public boolean verifOp(String condition , String operateur) {
		int pos = condition.indexOf(operateur);
		
		if(pos==-1) {
			return false ;
		}
		return true ;
	}


	/**	verifier si un operateur fait parti d'une condition 
	 *  @param condition
	 *	@param operateur
	 *  @throws IOException
	 */
	public List<String> verifCondition(String condition,List<Record>listerecord) {
		List<String> liste=new ArrayList<String>();
		
		if(verifOp(condition,"=" )&& verifOp(condition,">=")==false&& verifOp(condition,"<=")==false) {
			String[] tab1 =condition.split("=");
			
			for (int i=0;i<listerecord.get(0).getRelationInfo().getNomcol().size();i++) {
				
				if(listerecord.get(0).getRelationInfo().getNomcol().get(i).equals(tab1[0])) {
					
					for (int j=0;j<listerecord.size();j++) {
						
						if(tab1[1].equals(listerecord.get(j).getValues().get(i).toString())) {
							liste.add(listerecord.get(j).getValues().toString());
						}
					}
				}
			}
		}
	
		if(verifOp(condition,"<")&& verifOp(condition,"<=")==false) {
			
			String[] tab1 =condition.split("<");
			
			for (int i=0;i<listerecord.get(0).getRelationInfo().getNomcol().size();i++) {
				if(listerecord.get(0).getRelationInfo().getNomcol().get(i).equals(tab1[0])) {
				
					for (int j=0;j<listerecord.size();j++) {
						if(listerecord.get(0).getRelationInfo().getType().get(i).equals("int")) {
							
							if(Integer.parseInt(listerecord.get(j).getValues().get(i).toString())<Integer.parseInt(tab1[1])) {
								liste.add(listerecord.get(j).getValues().toString());
							}
						}
						if(listerecord.get(0).getRelationInfo().getType().get(i).equals("float")) {
							
							if(Float.parseFloat(listerecord.get(j).getValues().get(i).toString())<Float.parseFloat(tab1[1])) {
								liste.add(listerecord.get(j).getValues().toString());
							}
						}		
					}	
				}
			}
		}
		if(verifOp(condition,">")&& verifOp(condition,">=")==false) {
			
			String[] tab1 =condition.split(">");
			
			for (int i=0;i<listerecord.get(0).getRelationInfo().getNomcol().size();i++) {
				if(listerecord.get(0).getRelationInfo().getNomcol().get(i).equals(tab1[0])) {
					
					for (int j=0;j<listerecord.size();j++) {
						if(listerecord.get(0).getRelationInfo().getType().get(i).equals("int")) {
							
							if(Integer.parseInt(listerecord.get(j).getValues().get(i).toString())>Integer.parseInt(tab1[1])) {
								liste.add(listerecord.get(j).getValues().toString());
							}
						}
						if(listerecord.get(0).getRelationInfo().getType().get(i).equals("float")) {
							
							if(Float.parseFloat(listerecord.get(j).getValues().get(i).toString())>Float.parseFloat(tab1[1])) {
								liste.add(listerecord.get(j).getValues().toString());
							}
						}	
					}		
				}
			}
		}	
		if(verifOp(condition,"<=")) {
			
			String[] tab1 =condition.split("<=");
			
			for (int i=0;i<listerecord.get(0).getRelationInfo().getNomcol().size();i++) {
				if(listerecord.get(0).getRelationInfo().getNomcol().get(i).equals(tab1[0])) {
					
					for (int j=0;j<listerecord.size();j++) {
						if(listerecord.get(0).getRelationInfo().getType().get(i).equals("int")) {
							
							if(Integer.parseInt(listerecord.get(j).getValues().get(i).toString())<=Integer.parseInt(tab1[1])) {
								liste.add(listerecord.get(j).getValues().toString());
							}
						}
						if(listerecord.get(0).getRelationInfo().getType().get(i).equals("float")) {
							
							if(Float.parseFloat(listerecord.get(j).getValues().get(i).toString())<=Float.parseFloat(tab1[1])) {
								liste.add(listerecord.get(j).getValues().toString());
							}
						}	
					}		
				}
			}
		}
		if(verifOp(condition,">=")) {
			
			String[] tab1 =condition.split(">=");
			
			for (int i=0;i<listerecord.get(0).getRelationInfo().getNomcol().size();i++) {
				if(listerecord.get(0).getRelationInfo().getNomcol().get(i).equals(tab1[0])) {
					
					for (int j=0;j<listerecord.size();j++) {
						if(listerecord.get(0).getRelationInfo().getType().get(i).equals("int")) {
							
							if(Integer.parseInt(listerecord.get(j).getValues().get(i).toString())>=Integer.parseInt(tab1[1])) {
								liste.add(listerecord.get(j).getValues().toString());
							}
						}
						if(listerecord.get(0).getRelationInfo().getType().get(i).equals("float")) {
							
							if(Float.parseFloat(listerecord.get(j).getValues().get(i).toString())>=Float.parseFloat(tab1[1])) {
								liste.add(listerecord.get(j).getValues().toString());
							}
						}	
					}				
				}
			}
		}
		
		return liste;
	}


	/**	selectionne avec une condition
	 *  @param commandeSS
	 *  @throws IOException
	 */

	public void selects(String[] commandeSS) throws IOException {
	String nomRelation = commandeSS[2];
		List<Record>listeRec=new ArrayList<Record>();

		
		listeRec.addAll(filemanager.SelectcFromRelation(nomRelation));
		List<String> liste = new ArrayList<String>();
		liste=verifCondition(commandeSS[4],listeRec);
		try {
	
			for (int i = 0; i < liste.size(); i++) {
			
				String s=liste.get(i).toString();
				s=s.replace("[","");
				s=s.replace("]"," . ");
				s=s.replace(","," ;");
				System.out.print(s);
			
				System.out.println();
			}
			System.out.println("Total records=" + liste.size());

		}catch(NullPointerException e) {
			System.out.println("Il n'y a pas de record pour la relation  : "+nomRelation);
		}
	}


	/**	selectionne avec une condition
	 *  @param commandeSC
	 *  @throws IOException
	 */
	public void selectc(String[] commandeSC) throws IOException {
		String nomRelation = commandeSC[2];
		List<String> selectc =new ArrayList<String>();

		HashMap<Integer, List<String>> mapcondi= new HashMap<Integer, List<String>>();
		
		ArrayList<Record> listeDerecord = new ArrayList<Record>();
		ArrayList<String> liste = new ArrayList<String>();
	
		HashSet <String> hashsetfirst = new HashSet <String>();
	    HashSet <String> hashsetsecond = new HashSet <String>();


		for (int i=4;i<commandeSC.length;i++) {
			if (commandeSC[i].equals("AND")) {
				continue;
			}
			else {
				selectc.add(commandeSC[i]);
			}
		}
		// hash map pour mettre la condition dans la case key et ses record dans la case value  

		listeDerecord.addAll(filemanager.SelectcFromRelation(nomRelation));
		
		
		for(int i=0;i<selectc.size();i++) {
		
			mapcondi.put(i,verifCondition(selectc.get(i),listeDerecord));
			
		}
	

	    for (int k=0;k<mapcondi.get(0).size();k++) {
			hashsetfirst.add(mapcondi.get(0).get(k).toString());
	    }
	     
	    mapcondi.remove(0);
	     
		for (Integer i : mapcondi.keySet()) {
			for (int k=0;k<mapcondi.get(i).size();k++) {
				hashsetsecond.add(mapcondi.get(i).get(k).toString());
			}
			// intersection des case value pour prendre juste se qu'ils ont les meme record
			hashsetfirst.retainAll(hashsetsecond); 
		}
		
		for(String lf : hashsetfirst) {
			lf=lf.replace("[","");
			lf=lf.replace("]","");
			lf=lf.replace(","," ;");
			 System.out.print(lf); System.out.println(".");
				
		}

		System.out.println("Total records=" +hashsetfirst.size()+"," );
		
	}


}