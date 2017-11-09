public interface ReadWriteLock {
    public void acquireReadLock(int readerId);
    public void acquireWriteLock(int writerId);
    public void releaseReadLock(int readerId);
    public void releaseWriteLock(int writerId);
}