public class Dropbox {
    private int number;
    private boolean evenNumber = false;
    private boolean consumed = true;
    public int take(final boolean even) {
        if ((even == evenNumber) && (!consumed)) {
            System.out.format("%s CONSUMIDOR obtem %d.%n", even ? "PAR" : "IMPAR", number);
            consumed = true;
        }
        return number;
    }
    public void put(int number) {
        if (consumed) {
            this.number = number;
            evenNumber = number % 2 == 0;
            System.out.format("PRODUTOR gera %d.%n", number);
            consumed = false;
        }
    }
}