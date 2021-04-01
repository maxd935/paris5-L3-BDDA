import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class DiskManager {
	private static DiskManager INSTANCE;

	private DiskManager() {
	}

	public static synchronized DiskManager getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new DiskManager();
			return INSTANCE;
		} else
			return INSTANCE;
	}


	/**
	 * Creation fichier
	 * 
	 * @param fileIdx 
	 * @throws IOException
	 */
	public void CreateFile(int fileIdx) throws IOException {
		FileWriter fichier = new FileWriter(DBParams.DBPath + "/Data_" + fileIdx + ".rf");
		fichier.close();
	}


	/**
	 * ajouter une page
	 * 
	 * @param fileIdx 
	 * @return l'id
	 * @throws IOException
	 */
	public PageId AddPage(int fileIdx) throws IOException {
		RandomAccessFile fichier = new RandomAccessFile(new File(DBParams.DBPath + "/Data_" + fileIdx + ".rf"), "rw");
		fichier.seek(fichier.length());
		for(int i=0;i<DBParams.pageSize;i++) {
			fichier.write((byte) 0);
		}
		PageId id = new PageId(((int) fichier.length() / DBParams.pageSize) - 1, fileIdx);
		
		fichier.close();

		return id;
	}


	/**
	 * lire une page du fichier 
	 * @param pageId
	 * @param buff 
	 * @throws IOException
	 */
	public void ReadPage(PageId pageId, ByteBuffer buff) throws IOException {
		RandomAccessFile file = new RandomAccessFile(DBParams.DBPath + "/Data_" + pageId.getFileIdx() + ".rf", "r");
		int position = pageId.getPageIdx() * DBParams.pageSize;
	
			file.seek(position);
			file.read(buff.array());
			file.close();
	}

	
	/**
	 * ecrire dans une page du fichier 
	 * @param pageId
	 * @param buff 
	 * @throws IOException
	 */
	public void Writepage(PageId pageId, ByteBuffer buff) throws IOException {
		RandomAccessFile file = new RandomAccessFile(DBParams.DBPath + "/Data_" + pageId.getFileIdx() + ".rf", "rw");
		int pos = pageId.getPageIdx() * DBParams.pageSize;
		file.seek(pos);
		file.write(buff.array());
		file.close();

	}

}
