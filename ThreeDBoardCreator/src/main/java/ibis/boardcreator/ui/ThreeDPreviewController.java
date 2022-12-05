package ibis.boardcreator.ui;

import ibis.boardcreator.datamodel.Grid;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

public class ThreeDPreviewController extends Application {

  private static final float WIDTH = 700;
  private static final float HEIGHT = 700;

  private double anchorX, anchorY;
  private double anchorAngleX = 0;
  private double anchorAngleY = 0;
  private final DoubleProperty angleX = new SimpleDoubleProperty(0);
  private final DoubleProperty angleY = new SimpleDoubleProperty(0);

  @Override
  public void start(Stage primaryStage) {
    

    SmartGroup group = new SmartGroup();
    prepareBox(group);
    Grid grid = App.getGrid();
    Camera camera = new PerspectiveCamera(true);
    camera.setNearClip(1);
    camera.setFarClip(1000);
    camera.translateZProperty().set(-500);
    camera.translateXProperty().set((grid.getNumRows()/2) * 10);
    camera.translateYProperty().set((grid.getNumColumns()/2) * 10);

    Scene scene = new Scene(group, WIDTH, HEIGHT,true);
    scene.setFill(Color.SILVER);
    scene.setCamera(camera);

    group.translateXProperty().set(0);
    group.translateYProperty().set(0);
    group.translateZProperty().set(0);

    initMouseControl(group, scene, primaryStage);
    primaryStage.setTitle("3D Preview");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void prepareBox(Group group) {
	Grid grid = App.getGrid();
    for (int row = 0;  row< grid.getNumRows(); row++) {
    	for (int col = 0; col < grid.getNumColumns(); col++) {
    		Box box = new Box(10, 10,grid.getTileAt(row, col).getElevation()*5);
    		group.getChildren().add(box);
    		box.setTranslateX(col*10);
    		box.setTranslateY(row*10);
    		box.setTranslateZ(-grid.getTileAt(row, col).getElevation()*5 / 2);
    	}
    }

  }

  private void initMouseControl(SmartGroup group, Scene scene, Stage stage) {
    Rotate xRotate;
    Rotate yRotate;
    group.getTransforms().addAll(
        xRotate = new Rotate(0, Rotate.X_AXIS),
        yRotate = new Rotate(0, Rotate.Y_AXIS)
    );
    xRotate.angleProperty().bind(angleX);
    yRotate.angleProperty().bind(angleY);

    scene.setOnMousePressed(event -> {
      anchorX = event.getSceneX();
      anchorY = event.getSceneY();
      anchorAngleX = angleX.get();
      anchorAngleY = angleY.get();
    });

    scene.setOnMouseDragged(event -> {
      angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
      angleY.set(anchorAngleY + anchorX - event.getSceneX());
    });

    stage.addEventHandler(ScrollEvent.SCROLL, event -> {
      double delta = event.getDeltaY();
      group.translateZProperty().set(group.getTranslateZ() + delta);
    });
  }

  public static void main(String[] args) {
    launch(args);
  }

  class SmartGroup extends Group {

    Rotate r;
    Transform t = new Rotate();

    void rotateByX(int ang) {
      r = new Rotate(ang, Rotate.X_AXIS);
      t = t.createConcatenation(r);
      this.getTransforms().clear();
      this.getTransforms().addAll(t);
    }

    void rotateByY(int ang) {
      r = new Rotate(ang, Rotate.Y_AXIS);
      t = t.createConcatenation(r);
      this.getTransforms().clear();
      this.getTransforms().addAll(t);
    }
  }
}