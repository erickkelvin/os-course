class SleepUtilities {
    public static void nap() {
        int randomTime = (int) (Math.random() * 4);
        try {
            Thread.sleep(randomTime*1000);
        }
        catch (InterruptedException e) {}
    }
}