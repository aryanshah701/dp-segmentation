//A class that stores the optimal value and start value table
class DPInformation {

  int[][] opt;
  int[][] start;

  DPInformation(int[][] opt, int[][] start) {
    this.opt = opt;
    this.start = start;
  }

  //Getter methods
  public int[][] getOpt() {
    return opt;
  }

  public int[][] getStart() {
    return start;
  }
}
