//A class that represents a partition(the camera and the start index)
class Partition {

  Camera camera;
  int end;

  Partition(Camera camera, int end) {
    this.camera = camera;
    this.end = end;
  }

  @Override
  public String toString() {
    return camera.toString() + end;
  }
}
