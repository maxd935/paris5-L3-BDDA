import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;




public class DBInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static DBInfo INSTANCE;
	private List<RelationInfo> relationinfo;
	private int counter;


	//constructeur DBInfo
	private DBInfo() 
	{
		this.relationinfo = new ArrayList<RelationInfo>();
	}
	
	public static synchronized DBInfo getInstance() {
		
		if(INSTANCE == null) 
		{
			INSTANCE =  new DBInfo();
			return INSTANCE;
		}
		else 
			return INSTANCE;
	}	
		
	/**
	 * Fonction de sauvgarde de compteur et relation
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */	
	public void finish() throws IOException {
		File saveFile=new File(DBParams.DBPath+"/Catalog.def");
		FileOutputStream file= new FileOutputStream(saveFile);
		ObjectOutputStream out= new ObjectOutputStream(file);
		out.writeObject(DBInfo.getInstance());
		out.close();
		file.close();
	}


	/**
	 * Fonction d'initialisation
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */	
	public void init() throws IOException, ClassNotFoundException 
	{
		File saveFile = new File(DBParams.DBPath + "/Catalog.def");
		if (saveFile.exists()) {
			FileInputStream file;
			ObjectInputStream in;
			try {
				file = new FileInputStream(saveFile);
				in = new ObjectInputStream(file);
				DBInfo dbinfo = (DBInfo) in.readObject();

				this.relationinfo = dbinfo.relationinfo;
				this.counter = dbinfo.counter;
				in.close();
				file.close();
			} catch (FileNotFoundException err) {
				err.printStackTrace();
			} catch (IOException err) {
				err.printStackTrace();
			} catch (ClassNotFoundException err) {
				err.printStackTrace();
			}
		}
	}
		
		
	public int getCompteur() 
	{
		return counter;
	}
	
	public void setCompteur(int counter) 
	{
		this.counter = counter;
	}

	public void setDefinition(List<RelationInfo>  relationinfo) 
	{
		this. relationinfo =  relationinfo;
	}
		
	public List<RelationInfo> getRelationInfo() 
	{
		return  relationinfo;
	}
	
	/**
	 * Fonction qui rajoute la relation et counter+1
	 * @param relation relationinfo
	 */	
	public void addRelation(RelationInfo relation)
	{
		 relationinfo.add(relation);
		this.counter++;
	}
	

	/**
	 * fonction  réeinitialisation 
	 * 
	 */	
	public void reset() 
	{
		this. relationinfo.clear();
		this.setCompteur(0);
	}	
		
}
