import java.nio.ByteBuffer;

public class Frame  {
		private ByteBuffer buffer;
		private PageId pageId;
		private int pinCount;
		private int dirty;
		private long leastused;
		
		
		public Frame() {
			this.buffer=ByteBuffer.allocate(DBParams.pageSize);
			this.pageId = null;
			this.pinCount=0;
			this.dirty=0;
			this.leastused=-1;
		}


		public ByteBuffer getBuffer() {
			return buffer;
		}


		public void setBuffer(ByteBuffer buffer) {
			this.buffer = buffer;
		}


		public PageId getPageId() {
			return pageId;
		}


		public void setPageId(PageId pageId) {
			this.pageId = pageId;
		}
	

		public int getDirty() {
			return dirty;
		}


		public void setDirty(int dirty) {
			if (dirty==1) this.dirty=1;	
		}


		public long getLeastused() {
			return leastused;
		}


		public void setLeastused(long leastused) {
			this.leastused = leastused;
		}


		public int getPinCount() {
			return pinCount;
		}
		
		
		public void incrementerPin() {
			this.pinCount++;
		}
		
		public void decrementerPin() {
			if (this.pinCount>0) this.pinCount--;
			if (this.pinCount==0) this.leastused=System.currentTimeMillis();
		}

		
		
		
}
