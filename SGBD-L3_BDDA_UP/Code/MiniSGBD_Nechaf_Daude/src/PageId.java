
public class PageId {
	private int fileIdx;
	private int pageIdx;

	public PageId(int pageIdx, int fileIdx) {
		this.pageIdx = pageIdx;
		this.fileIdx = fileIdx;
	}

	public int getFileIdx() {
		return fileIdx;
	}

	public int getPageIdx() {
		return pageIdx;
	}

	boolean equals(PageId id2) {
			return this.fileIdx == id2.fileIdx && this.pageIdx == id2.pageIdx;
	}

	public void setFileIdx(int fileIdx) {
		this.fileIdx = fileIdx;
	}

	public void setPageIdx(int pageIdx) {
		this.pageIdx = pageIdx;
	}
}
