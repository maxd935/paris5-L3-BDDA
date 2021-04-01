import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BufferManager {
	private static BufferManager INSTANCE;
	private ArrayList<Frame> bufferp;


	private BufferManager() {
		bufferp = new ArrayList<Frame>();
		for (int i = 0; i < DBParams.frameCount; i++) {
			bufferp.add(new Frame());
		}
	}


	public static synchronized BufferManager getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new BufferManager();
			return INSTANCE;
		} else
			return INSTANCE;
	}


	public ByteBuffer getPage(PageId pageId) throws IOException {

		for (int i = 0; i < bufferp.size(); i++) {
			Frame fr = bufferp.get(i);
			if (fr.getPageId() == null)
				continue;
			if (fr.getPageId().equals(pageId)) {
				fr.incrementerPin();
				return fr.getBuffer();
			}
		}

		for (int i = 0; i < bufferp.size(); i++) {
			Frame fr = bufferp.get(i);
			if (fr.getPageId() == (null)) {
				DiskManager.getInstance().ReadPage(pageId, fr.getBuffer());
				fr.setPageId(pageId);
				fr.incrementerPin();
				return fr.getBuffer();
			}
		}

		for (int i = 0; i < bufferp.size(); i++) {
			Frame fr = bufferp.get(i);
			if (fr.getPinCount() == 0) {
				if (fr.getDirty() == 1) {
					DiskManager.getInstance().Writepage(fr.getPageId(), fr.getBuffer());
				}

				fr.setDirty(0);
				DiskManager.getInstance().ReadPage(pageId, fr.getBuffer());
				fr.setPageId(pageId);
				fr.incrementerPin();
				return fr.getBuffer();
			}
		}
		
		return null;
	}


	public void freePage(PageId pageId, int valdirty) {
		for (int i = 0; i < bufferp.size(); i++) {
			Frame fr = bufferp.get(i);
			if (fr.getPageId() == null)
				continue;
			if (fr.getPageId().equals(pageId)) {
				fr.decrementerPin();
				fr.setDirty(valdirty);
				return;
			}
		}
	}


	public void flushAll() throws IOException {
		for (int i = 0; i < bufferp.size(); i++) {
			Frame fr = bufferp.get(i);
			if (fr.getDirty() == 1) {
				DiskManager.getInstance().Writepage(fr.getPageId(), fr.getBuffer());
			}
			bufferp.set(i, new Frame());
			fr = new Frame();
		}
	}


	public void reset() throws IOException {
		flushAll();
	}
}
