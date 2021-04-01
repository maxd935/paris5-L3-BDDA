import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;



public class HeapFile {
	private RelationInfo relinfo;
	DiskManager diskmanager = DiskManager.getInstance();
	BufferManager buffermanager = BufferManager.getInstance();

	public HeapFile(RelationInfo relinfo) {
		this.relinfo = relinfo;
	}
	
	public RelationInfo getRelationInfo() {
		return relinfo;
	}

		

	/**
	 * 
	 * 
	 * @return le pageid 
	 * @throws IOException
	 */
	public PageId addDataPage() throws IOException {

		PageId id = diskmanager.AddPage(relinfo.getFileIdx());

		int nbpages = id.getPageIdx();

		PageId entetepage = new PageId(0, id.getFileIdx());


		ByteBuffer buff = buffermanager.getPage(entetepage);

		buff.position(0);
		buff.putInt(nbpages);
		buff.position(nbpages * 4);
		buff.putInt(relinfo.getSlotCount());

		buffermanager.freePage(entetepage, 1);
		return id;
	}


	/**
	 * 
	 * 
	 * @return le pageid 
	 * @throws IOException
	 */
	public PageId getFreeDataPageId() throws IOException {


		int idfichier = relinfo.getFileIdx();

		PageId pageid = new PageId(0, idfichier);

		ByteBuffer buff = buffermanager.getPage(pageid);

		buff.position(0);

		int nbrepages = buff.getInt();

		for (int i = 0; i < nbrepages; i++) {


			if (buff.getInt() > 0) {
				buffermanager.freePage(pageid, 1);
				return new PageId(i + 1, this.relinfo.getFileIdx());
			}
		}
		buffermanager.freePage(pageid, 1);
		return null;
	}


	/**
	 *
	 * 
	 * @param record le record 
	 * @param pageid la pageid
	 * @return le rid 
	 * @throws IOException
	 */
	public Rid writeRecordToDataPage(Record record, PageId pageid) throws IOException {
		ByteBuffer page = buffermanager.getPage(pageid);
		PageId hd = new PageId(0, pageid.getFileIdx());
		ByteBuffer buff = buffermanager.getPage(hd);
		int nbeslots = relinfo.getSlotCount();
		boolean isfind = false;
		page.position(0);
		while (nbeslots >= 1 && (isfind == false)) {
			nbeslots--;
			if (page.get() == 0) {
				isfind = true;
			}
		}
		int position = page.position() - 1;
		page.put(position, (byte) 1);
		record.WriteToBuffer(page, relinfo.getSlotCount() + position * relinfo.getRecordSize());
		buffermanager.freePage(pageid, 1);
		buff.position(pageid.getPageIdx() * 4);
		int aux = buff.getInt();
		buff.position(pageid.getPageIdx() * 4);
		buff.putInt(aux - 1);
		buffermanager.freePage(hd, 1);

		return new Rid(pageid, relinfo.getSlotCount());
	}


	/**
	 *  
	 * @throws IOException
	 */
	public void createNewOnDisk() throws IOException {
		diskmanager.CreateFile(relinfo.getFileIdx());
		PageId pid = diskmanager.AddPage(relinfo.getFileIdx());
		ByteBuffer buf = buffermanager.getPage(pid);
		buf.position(0);
		for (int i = 0; i < DBParams.pageSize; i++) {
			buf.put((byte) 0);
		}

		buffermanager.freePage(pid, 1);
	}


	/**
	 * 
	 * 
	 * @param pageId le pageid 
	 * @return la liste 
	 * @throws IOException
	 */
	public ArrayList<Record> getRecordsInDataPage(PageId pageId) throws IOException {
		ArrayList<Record> records = new ArrayList<Record>();
		ByteBuffer buffer = buffermanager.getPage(pageId);
		buffer.position(0);
		for (int i = 0; i < relinfo.getSlotCount(); i++) {
			
			if (buffer.get(i) == (byte) 1) {
				Record record = new Record();
				record.setRelationInfo(this.relinfo);
				record.readFromBuffer(buffer, relinfo.getSlotCount() + i * relinfo.getRecordSize());
				records.add(record);
			}
		}
		buffermanager.freePage(pageId, 0);
		return records;
	}



	/**
	 * fonction Insertion 
	 * 
	 * @param record le record en param
	 * @return un rid
	 * @throws IOException
	 */
	public Rid InsertRecord(Record record) throws IOException {

		record.setRelationInfo(this.relinfo);

		Rid rid;

		PageId dpId = getFreeDataPageId();

			if (dpId == null) {

				dpId = addDataPage();

			}

		return rid = writeRecordToDataPage(record, dpId);
	}


	/**
	 * 
	 * @return une liste de record
	 * @throws IOException
	 */

	public ArrayList<Record> getAllRecords() throws IOException {
		ArrayList<Record> liste = new ArrayList<Record>();

		int fileIdx = relinfo.getFileIdx();

		PageId headerPageId = new PageId(0, fileIdx);

		ByteBuffer byteBuffer = buffermanager.getPage(headerPageId);

		int nbrepages = byteBuffer.getInt(0);

			for (int i = 1; i <= nbrepages; i++) {
				PageId pageId = new PageId(i, fileIdx);
				liste.addAll(getRecordsInDataPage(pageId));
			}
		buffermanager.freePage(headerPageId, 1);
		return liste;
	}


	public ArrayList<PageId> getAllDataPage() throws IOException {
		ArrayList<PageId> listePageid = new ArrayList<PageId>();

		int fildx = relinfo.getFileIdx();

		PageId head = new PageId(0, fildx);

		ByteBuffer header = buffermanager.getPage(head);

		int x = header.getInt(0);

		buffermanager.freePage(head, 1);

			for (int i = 0; i < x; i++) {
				PageId page = new PageId(i + 1, fildx);
				listePageid.add(page);

			}

		return listePageid;
	}

	

}
