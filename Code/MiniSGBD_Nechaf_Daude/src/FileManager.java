import java.io.IOException;
import java.util.ArrayList;

public class FileManager {
	DBInfo dbinfo = DBInfo.getInstance();
	private ArrayList<HeapFile> heapFiles;
	private static FileManager INSTANCE;

	private FileManager() {
		heapFiles = new ArrayList<HeapFile>();
	}

	public static synchronized FileManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FileManager();
			return INSTANCE;
		} else
			return INSTANCE;
	}

	public void init() {
		for (int i = 0; i < dbinfo.getRelationInfo().size(); i++) {
			HeapFile heapfile = new HeapFile(dbinfo.getRelationInfo().get(i));
			heapFiles.add(heapfile);

		}

	}


	/**
	 * creation de relation
	 * 
	 * @param relinfo 
	 * @throws IOException
	 */
	public void CreateRelationFile(RelationInfo relinfo) throws IOException {
		HeapFile heapfile = new HeapFile(relinfo);
		heapFiles.add(heapfile);
		heapfile.createNewOnDisk();

	}


	/**
	 * inserer un record
	 * @param record
	 * @param nomrel 
	 * @return le rid
	 * @throws IOException
	 */
	public Rid InsertRecordInRelation(Record record, String nomrel) throws IOException {
		Rid rid = null;
		for (int i = 0; i < heapFiles.size(); i++) {

			if (heapFiles.get(i).getRelationInfo().getNom().equals(nomrel)) {
				rid = heapFiles.get(i).InsertRecord(record);
				return rid;

			}
		}

		return rid;
	}


	/**
	 * Selectionner tous les record
	 * 
	 * @param nomrel 
	 * @return liste des record
	 * @throws IOException
	 */
	public ArrayList<Record> SelectAllFromRelation(String nomrel) throws IOException {
		ArrayList<Record> liste = null;
		for (int i = 0; i < heapFiles.size(); i++) {
			if (heapFiles.get(i).getRelationInfo().getNom().equals(nomrel)) {
				liste = heapFiles.get(i).getAllRecords();
			}
		}
		return liste;
	}


	/**
	 * selectionner selon les condition 
	 * 
	 * @param nomrel 
	 * @return liste des record 
	 * @throws IOException
	 */
	public ArrayList<Record> SelectcFromRelation(String nomrel ) throws IOException {
		ArrayList<Record> liste = new ArrayList<Record>();
		ArrayList<Record> listeii = new ArrayList<Record>();
		liste = SelectAllFromRelation(nomrel );
		for (int i = 0; i < liste.size(); i++) {
				listeii.add(liste.get(i));
		}

		return listeii;
	}


	public ArrayList<HeapFile> getHeapFiles() {
		return heapFiles;
	}


	public void reset() throws IOException {
		heapFiles.clear();
	}

}
