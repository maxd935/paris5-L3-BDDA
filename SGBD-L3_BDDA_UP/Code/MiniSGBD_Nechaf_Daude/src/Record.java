
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Record {

	private RelationInfo relinfo;
	private List<String> values;

	//Ajout d'un constructeur a vide
	public Record() {
		values = new ArrayList<String>();
	}
	
	public RelationInfo getRelationInfo() {
		return relinfo;
	}

	public Record(RelationInfo relinfo) 
	{
		this.relinfo = relinfo;
			values = new ArrayList<String>();
	}
	
	
	
	public void setRelationInfo(RelationInfo relinfo) 
	{
		this.relinfo = relinfo;
	}

	public List<String> getValues() 
	{
		return values;
	}
	
	public void setValues(String values) {
		this.values.add(values);
	}


	/**
	 *
	 * fonction qui mets dans le buffer la valeur du record
	 * @param buff 
	 * @param position  
	 */
	public void WriteToBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		for (int i = 0; i < values.size(); i++) {
			
				//si le type est un int
			if (relinfo.getType().get(i).equals("int")) {
				buff.putInt(Integer.parseInt(values.get(i)));
				//si le type est un float	
			} else if (relinfo.getType().get(i).equals("float")) {
				buff.putFloat(Float.parseFloat(values.get(i)));
			}
				//si le type est un stringX ou x est la taille du string
			 else if (relinfo.getType().get(i).substring(0,6).equals("string")) {
				
				for (int j = 0; j < values.get(i).length(); j++) {
					buff.putChar(values.get(i).charAt(j));
				}
			}

		}
	}
	

	/**
	 *
	 * fonction qui lit le buffer
	 * @param buff 
	 * @param position  
	 */
	public void readFromBuffer(ByteBuffer buff,int position) {
		buff.position(position);
		
		for (int i = 0; i < relinfo.getType().size(); i++) {
				//si le type est un int
			if (relinfo.getType().get(i).equals("int")) 
			{
				values.add(String.valueOf(buff.getInt()));
				
			}
				//si le type est un float
			 else if (relinfo.getType().get(i).equals("float")) 
			 {
				values.add(String.valueOf(buff.getFloat()));
			 } 
			 	//si le type est un stringX ou x est la taille du string
			 else if (relinfo.getType().get(i).substring(0,6).equals("string")) 
			 {
				int valeur = Integer.parseInt(relinfo.getType().get(i).substring(6));
				StringBuilder sb = new StringBuilder();
				for (int l = 0; l <valeur; l++) {
				
					sb.append(String.valueOf(buff.getChar()));
				}
				values.add(sb.toString());
			}
		}		
	}

	
}

