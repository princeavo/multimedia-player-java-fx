package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.ErrorNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Accueil extends Application {

	static ObservableList<ModeleVideo> liste = FXCollections.observableArrayList();
	static TableView<ModeleVideo> table = new TableView<>();
	static StackPane playPane;
	static Scene scene1;
	static Stage stage;
	static Stage op;
	Liste defaut;
	static Stage fen ;
	static BooleanProperty isVisibleStage;
	static MediaPlayer[] player = new MediaPlayer[1];
	static Scene webScene;
	ComboBox<String> motifTri;
	ComboBox<String> ordreTri;
	ObservableList<String> ordreListe;
	static Class<? extends Accueil> cheminBase;
	final String continueChemin = "continue.png";
	static TableView<Liste> tableView ;
	ProgressBar progress;
	Label rechercheLabel;
	

	public static void main(String[] args) {
		launch(args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		progress = new ProgressBar();
		progress.setPrefWidth(500);
		progress.setPrefHeight(10);
		rechercheLabel = new Label("Recherche de vidéos en cours...");
		
		op = new Stage(StageStyle.TRANSPARENT);
		op.initOwner(stage);
		
		cheminBase = getClass();
		tableView = new TableView<>();
		fen = new Stage();
		fen.initStyle(StageStyle.UTILITY);
		fen.setAlwaysOnTop(true);
		fen.setResizable(true);
		
		
		
		
	
		/*
		 * Maintenant ce que je veux faire c'est qu'au démarrage les médias trouvées seront ajoutés dans défaut
		 * 
		 */
		
		
//		
		try {
//			String hj = "C:\\Users\\ADMIN\\Documents\\Itel A16plus\\Java\\Codes\\video\\src\\com\\listeDeLecture.prince";
			String hj = "listeDeLecture.prince";
			Alert gd = new Alert(AlertType.INFORMATION);
			gd.setHeaderText(hj);
			gd.setContentText(hj);
//			gd.showAndWait();
			FileInputStream fis = new FileInputStream(hj);
			ObjectInputStream oos = new ObjectInputStream(fis);
			Liste li;
			while((li = (Liste)oos.readObject()) != null) {
					tableView.getItems().add(li);
			}
			oos.close();
			fis.close();
		} catch (IOException e1) {
		}
		
		
		

		MenuBar menuBar = new MenuBar();

		motifTri = new ComboBox<>();
		ordreTri = new ComboBox<>();
		ordreListe = FXCollections.observableArrayList();

		MenuItem fichierItem = new MenuItem("Fichier");
		fichierItem.setStyle("-fx-text-fill:#124578");
		fichierItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Selectionnez une vidéo");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Tous les fichiers", "*.mp4;*.mp3;*.avi"));
			List<File> file = fileChooser.showOpenMultipleDialog(primaryStage);

			for (File fichier : file) {
				if (fichier != null) {
					new Thread(() -> {
						getVideos(fichier.getAbsolutePath(),tableView.getSelectionModel().getSelectedItem());
						trier();
					}).start();
				}
			}
		});

		MenuItem dossierItem = new MenuItem("Dossier");
		dossierItem.setStyle("-fx-text-fill:#124578");
		dossierItem.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Choisir un répertoire");
			File selectedDirectory = directoryChooser.showDialog(primaryStage);
			if (selectedDirectory != null) {
				new Thread(() -> {
					getVideos(selectedDirectory.getAbsolutePath(),tableView.getSelectionModel().getSelectedItem());
					trier();
				}).start();
			}
		});

		Menu fileMenu = new Menu("Open");
		fileMenu.setStyle("-fx-text-fill:#194570");

		fileMenu.getItems().addAll(fichierItem, dossierItem);
		Menu menu = new Menu("Menu",
				new ImageView(new Image(getClass().getResource("menu.png").toString(), 15, 15, false, false, false)),
				fileMenu);

		MenuItem sousMenu1 = new MenuItem("Créer une liste");
		sousMenu1.setOnAction(e -> {
			TextField nomField = new TextField();
			Label nomLabel = new Label("Veuillez entrer le nom de votre liste");
			Button submit = new Button("Créer");
			submit.setStyle("-fx-background-color:#136478;-fx-background-radius:10px;-fx-text-fill:#125746");
			submit.setPrefSize(100, 50);
			submit.setOnMouseEntered(event -> {
				submit.setStyle("-fx-background-color:#FFA07A;-fx-background-radius:10px;-fx-text-fill:#800000");
			});
			submit.setOnMouseExited(event -> {
				submit.setStyle("-fx-background-color:#136478;-fx-background-radius:10px;-fx-text-fill:#125746");
			});
			nomLabel.setTextFill(Color.SLATEBLUE);
			VBox createVBox = new VBox(10);
			createVBox.setPadding(new Insets(40));
			createVBox.setStyle("-fx-font-size:20px;-fx-background-color:rgba(128,0,0,0.725392292)");
			createVBox.getChildren().addAll(nomLabel, nomField, submit);
			createVBox.setAlignment(Pos.CENTER);
			Scene createScene = new Scene(createVBox);
			Stage createStage = new Stage();
			createStage.setScene(createScene);
			createStage.setTitle("Créer une liste de lecture");
			createStage.initStyle(StageStyle.DECORATED);
//			createStage.initModality(Modality.WINDOW_MODAL);
			createStage.initOwner(primaryStage);
			createStage.setResizable(false);
			createStage.initStyle(StageStyle.TRANSPARENT);
			createStage.focusedProperty().addListener((v, o, n) -> {
				if (Boolean.FALSE.equals(n)) {
					Timeline timeline = new Timeline();
					timeline.getKeyFrames().add(new KeyFrame(Duration.millis(40), ev -> {
						createStage.setOpacity(createStage.getOpacity() / 1.3);
					}));
					timeline.setCycleCount(Animation.INDEFINITE);
					timeline.play();
					if (createStage.getOpacity() < 1 / 64)
						createStage.hide();
				}
			});
			createStage.showAndWait();
		});

		MenuItem sousMenu2 = new MenuItem("Gérer vos listes");

		Menu lectureMenu = new Menu("Vos listes de lecture");
		lectureMenu.getItems().addAll(sousMenu1, sousMenu2);

		MenuItem webItem = new MenuItem("Naviguer sur le web");

		webItem.setOnAction(e -> {
			WebView webView = new WebView();

			webView.getEngine().load("https://youtube.com");
			webScene = new Scene(new StackPane(webView), Screen.getPrimary().getVisualBounds().getWidth() - 5,
					Screen.getPrimary().getVisualBounds().getHeight() - 25);
			primaryStage.setScene(webScene);
		});

		menu.getItems().addAll(lectureMenu, webItem);
		menu.setStyle("-fx-background-color:#124578;-fx-background-radius:10px;-fx-font-size:15px;");

		menuBar.getMenus().add(menu);
		menuBar.setTooltip(new Tooltip("Menu"));
		menuBar.setStyle("-fx-margin:20px");

		playPane = new StackPane();
//		scene1 = new Scene(playPane,500,310);
		scene1 = new Scene(playPane, Screen.getPrimary().getVisualBounds().getWidth(),
				Screen.getPrimary().getVisualBounds().getHeight() - 30);
		stage = new Stage();
		isVisibleStage = new SimpleBooleanProperty(false);
//		isVisibleStage.bind(stage.showingProperty());

//		isVisibleStage.addListener((v, o, n) -> {
//			if (Boolean.TRUE.equals(n)) {
//				System.out.println("Stage est visible");
//			} else {
//				System.out.println("Stage n'est pas visible");
//			}
//		});

		/*---------------------Ceci est le top du root-----------------*/

		Label label = new Label("Music Player");
		label.setId("titre");
//		label.setFont(Font.font("Dancing Script", FontWeight.EXTRA_BOLD, 40));
		label.setTextFill(Color.ANTIQUEWHITE);
		label.setPadding(new Insets(20));

		Label dateText = new Label();
		dateText.setTextAlignment(TextAlignment.RIGHT);
		dateText.setTextFill(Color.web("#758496"));

		HBox boxDate = new HBox(20);
		boxDate.getChildren().add(dateText);
		boxDate.setStyle("-fx-font-size:18px");
		boxDate.setAlignment(Pos.CENTER_RIGHT);
		boxDate.setPadding(new Insets(0, 20, 0, 20));

		HBox top1 = new HBox(20);
		top1.getChildren().addAll(label, boxDate);
		HBox.setHgrow(boxDate, Priority.SOMETIMES);

		motifTri.getItems().addAll("Nom", "Date", "Taille");
		motifTri.setPromptText("Selectionnez le motif du tri");
		motifTri.setTooltip(new Tooltip("Selectionnez le motif du tri"));

		ordreTri.setItems(ordreListe);
		ordreTri.setPromptText("Sélectionnez l'ordre ");

		Button playAllButton = new Button("Tout lire");
		playAllButton.setPrefSize(120, 30);
		playAllButton.setGraphic(
				new ImageView(new Image(cheminBase.getResource(continueChemin).toString(), 30, 30, true, false)));
		playAllButton.setContentDisplay(ContentDisplay.RIGHT);
		playAllButton.setOnAction(e -> {
			if (stage.isShowing()) {
				player[0].stop();
//				stage.close();
			}
			if(playAllButton.getText().equals("Tout lire")) {
					
			}
			
			

			playVideo(new Image(getClass().getResource("pause.png").toString(), 30, 30, false, false, false),
					new Image(getClass().getResource(continueChemin).toString(), 30, 30, false, false, false),
					new Image(getClass().getResource("table.jpg").toString()),
					new Image(getClass().getResource("previous.png").toString(), 40, 40, false, false, false),
					new Image(getClass().getResource("next.png").toString(), 40, 40, false, false, false),
					new Image(getClass().getResource("fav.png").toString()), 0, true);
		});
		isVisibleStage.addListener((f, g, h) -> {
			if (h.booleanValue()) {
				if(player[0].getStatus().equals(MediaPlayer.Status.PLAYING)) {
					playAllButton.setGraphic(
						new ImageView(new Image(cheminBase.getResource("pause.png").toString(), 30, 30, true, false)));
					playAllButton.setText("En lecture...");
				}
			} else {
				playAllButton.setGraphic(new ImageView(
						new Image(cheminBase.getResource(continueChemin).toString(), 30, 30, true, false)));
				if (player[0].getStatus() == MediaPlayer.Status.PAUSED)
					playAllButton.setText("En pause");
				else
					playAllButton.setText("Tout lire");
			}
		});

		HBox top2 = new HBox(10);

		TextField searchField = new TextField();
		searchField.setPromptText("Rechercher ici");
		searchField.setFocusTraversable(false);
		searchField.setTooltip(new Tooltip("Rechercher ici"));
//		ObservableList<ModeleVideo> listefiltre = liste.filtered(e -> {
//			return true;
//		});		
		int[] jk = { 0 };
		searchField.textProperty().addListener((v, o, n) -> {
			if (jk[0] == 0) {
				jk[0] = 10;
//				copy(listefiltre, liste);
			}
			String mot = searchField.getText().trim().toLowerCase();
			if (mot.isEmpty()) {
//				copy(listefiltre, liste);
				table.setItems(liste);
			} else {
//				Alert e = new Alert(AlertType.CONFIRMATION);
//				e.setHeaderText(listefiltre.size() + " ");
//				e.showAndWait();
//				copy(listefiltre.filtered(t -> {
//					return t.getChemin().toLowerCase().contains(mot);
//				}), liste);
//				Alert e1 = new Alert(AlertType.CONFIRMATION);
//				e1.setHeaderText(listefiltre.size() + " ");
//				e1.showAndWait();
				table.setItems(liste.filtered(t -> {
					return t.getNom().toLowerCase().contains(mot);
				}));
			}
//			table.setItems(liste);

//			listefiltre.remove(0);

//			liste.setAll(listefiltre);
//			e.setHeaderText(listefiltre.size()+" ");
//			e.showAndWait();

//			table.setItems(liste);
		});

		Label titre = new Label("Vos listes de lecture");
		titre.setFont(Font.font(30));

		TableColumn<Liste, String> nomColumn1 = new TableColumn<>("Nom");
		nomColumn1.setCellValueFactory(new PropertyValueFactory<>("nom"));

		TableColumn<Liste, String> dateColumn = new TableColumn<>("Date de créaction");
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

		

		tableView.getColumns().addAll(nomColumn1, dateColumn);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		if(tableView.getItems().isEmpty())
			tableView.getItems().add(new Liste("Liste par défaut"));

		tableView.getSelectionModel().select(0);
		defaut = tableView.getItems().get(0);

		tableView.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
			liste = FXCollections.observableArrayList(tableView.getSelectionModel().getSelectedItem().liste);
			table.setItems(liste);
//			Alert g = new Alert(AlertType.INFORMATION);
//			g.setHeaderText(tableView.getSelectionModel().getSelectedItem().getListe().size() + " table ");
//			g.setContentText(liste.size() + "  ");
//			g.showAndWait();
		});

		Button plus = new Button("",
				new ImageView(new Image(cheminBase.getResource("plus.png").toString(), 50, 50, true, false)));
		plus.setStyle("-fx-background-color:transparent");
		plus.setOnAction(e -> {
			tableView.getSelectionModel().getSelectedItem().setListe(new ArrayList<>(liste.subList(0, liste.size())));
//			Alert g = new Alert(AlertType.INFORMATION);
//			g.setHeaderText(defaut.getListe().size()+"  ");
//			g.showAndWait();
//			dateColumn.setContextMenu(new ContextMenu(fichierItem));

			Button listeDeleteButton = new Button("Supprimer");
			listeDeleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull()
					.or(tableView.getSelectionModel().selectedIndexProperty().isEqualTo(0)));
			listeDeleteButton.setOnAction(f -> {
				tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
			});

			TextField nomField = new TextField();
			nomField.setPromptText("Nom de la nouvelle liste");
			
			nomField.setOnKeyPressed(m->{
				if(m.getCode().equals(KeyCode.ENTER)) {
					if (!nomField.getText().trim().isEmpty())
						tableView.getItems().add(new Liste(nomField.getText()));
					nomField.clear();				}
			});
			
			Button listeAjouterButton = new Button("Ajouter");
			listeAjouterButton.setOnAction(f -> {
				if (!nomField.getText().trim().isEmpty())
					tableView.getItems().add(new Liste(nomField.getText()));
				nomField.clear();
			});

			HBox footerBox = new HBox(5);
			footerBox.getChildren().addAll(nomField, listeDeleteButton, listeAjouterButton);
			footerBox.setAlignment(Pos.CENTER_RIGHT);

			VBox listeBox = new VBox(10);
			listeBox.getChildren().addAll(titre, tableView, footerBox);
			listeBox.setAlignment(Pos.CENTER);
			listeBox.setPadding(new Insets(30));
//			listeBox.setStyle("-fx-background-color:#759462");

			Scene scene = new Scene(listeBox, 400, 500);
			Stage listeStage = new Stage(StageStyle.TRANSPARENT);
			listeStage.setScene(scene);
			listeStage.show();
			listeStage.focusedProperty().addListener((v, o, n) -> {
				if (Boolean.FALSE.equals(n)) {
					listeStage.hide();
				}
			});
		});

		top2.getChildren().addAll(searchField, motifTri, ordreTri, plus, playAllButton);
		top2.setAlignment(Pos.CENTER_RIGHT);
		top2.setPadding(new Insets(0, 20, 0, 20));

		VBox top = new VBox(10);
		top.getChildren().addAll(menuBar, top1, top2);

		/*---------------------Ceci est le centre du root-----------------*/
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		TableColumn<ModeleVideo, String> nomColumn = new TableColumn<>("Nom");
		nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
		nomColumn.setStyle("-fx-color:#425165;");

		TableColumn<ModeleVideo, String> modifColumn = new TableColumn<>("Modifié le");
		modifColumn.setCellValueFactory(new PropertyValueFactory<>("dateModification"));
		modifColumn.setStyle("-fx-color:#425165;");

		TableColumn<ModeleVideo, Integer> tailleColumn = new TableColumn<>("Taille");
		tailleColumn.setCellValueFactory(new PropertyValueFactory<>("taille"));
		tailleColumn.setStyle("-fx-color:#425165;");

		TableColumn<ModeleVideo, String> durationColumn = new TableColumn<>("Durée");
		durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
		durationColumn.setStyle("-fx-color:#425165;");

		table.getColumns().addAll(nomColumn, modifColumn, tailleColumn, durationColumn);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setStyle("-fx-background-color:#852741;");

		File file = new File(System.getProperty("user.home") + File.separator + "Videos");
		System.out.println(file.getAbsolutePath());
		
		
		table.setItems(liste);
		
		
		
		
		new Thread(() -> {
					getVideos(file.getAbsolutePath(),tableView.getItems().get(0));
					trier();
					Platform.runLater(() -> {
//						liste = FXCollections.observableList(tableView.getSelectionModel().getSelectedItem().liste);
						
//						Alert ghj = new Alert(AlertType.INFORMATION);
//						ghj.setHeaderText(liste.size()+"");
//						ghj.showAndWait();
						progress.setVisible(false);
						rechercheLabel.setVisible(false);
						if (liste.isEmpty()) {
							motifTri.setDisable(true);
							ordreTri.setDisable(true);
						}
					});
		}).start();
		
		
		
		
		
		
		
		
		
		
		
		
//		File[] fichiers = file.listFiles();
//		
//		
//		
//		
//		new Thread(() -> {
//			if (fichiers != null) {
//				for (File fichier : fichiers) {
//					getVideos(fichier.getAbsolutePath(),tableView.getItems().get(0));
//					trier();
//					Platform.runLater(() -> {
////						liste = FXCollections.observableList(tableView.getSelectionModel().getSelectedItem().liste);
//						table.setItems(liste);
//						if (liste.isEmpty()) {
//							motifTri.setDisable(true);
//							ordreTri.setDisable(true);
//						}
//					});
//				}
//			}
//		}).start();

		motifTri.valueProperty().addListener((v, o, n) -> {
			if (motifTri.getValue().equals("Date")) {
				ordreListe.setAll("Plus récent en premier", "Plus récent en dernier");
			} else if (motifTri.getValue().equals("Taille")) {
				ordreListe.setAll("Plus volumineux en premier", "Plus volumineux en dernier");
			} else {
				ordreListe.setAll("Ordre croissant", "Ordre décroissant");
			}

			if (!ordreListe.get(0).equals(ordreTri.getValue()) && !ordreListe.get(1).equals(ordreTri.getValue())) {
				ordreTri.setValue(ordreListe.get(0));

			}
			trier();
		});
		ordreTri.valueProperty().addListener((v, o, n) -> {

			if (motifTri.getValue() == null) {
				motifTri.setValue("Nom");
			}
			trier();
		});

		/*---------------------Ceci est le top du root-----------------*/
		BorderPane root = new BorderPane();
		root.setTop(top);
		root.setCenter(table);
		root.setBackground(new Background(new BackgroundImage(new Image(getClass().getResource("fond2.jpg").toString()),
				null, null, null, null)));
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String formString = format.format(date);
			dateText.setText(formString);
		}));

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth() - 5,
				Screen.getPrimary().getVisualBounds().getHeight() - 25);

		table.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				if (stage.isShowing()) {
					player[0].stop();
//					stage.close();
				}

				playVideo(new Image(getClass().getResource("pause.png").toString(), 30, 30, false, false, false),
						new Image(getClass().getResource("continue.png").toString(), 30, 30, false, false, false),
						new Image(getClass().getResource("table.jpg").toString()),
						new Image(getClass().getResource("previous.png").toString(), 40, 40, false, false, false),
						new Image(getClass().getResource("next.png").toString(), 40, 40, false, false, false),
						new Image(getClass().getResource("fav.png").toString()),
						table.getSelectionModel().getSelectedIndex(), false);
			}
		});

		HBox bottom = new HBox(10);

		Button deleteButton = new Button("");
		deleteButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
		deleteButton.setGraphic(
				new ImageView(new Image(cheminBase.getResource("delete.png").toString(), 50, 30, true, false)));
		deleteButton.setOnAction(e -> {

			RadioButton b1 = new RadioButton("Les éléments sélectionnés");
			b1.setSelected(true);

			RadioButton b2 = new RadioButton("Touts les éléments de la liste");
			ToggleGroup group = new ToggleGroup();
			group.getToggles().addAll(b1, b2);

			Stage deleteStage = new Stage(StageStyle.TRANSPARENT);
			VBox deleteBox = new VBox(10);
			deleteBox.setStyle("-fx-font-size:15px;-fx-background-color:#124578;");
			deleteBox.setPadding(new Insets(20));

			Button okButton = new Button("");
			okButton.setGraphic(
					new ImageView(new Image(cheminBase.getResource("check.png").toString(), 30, 30, false, false)));
			okButton.setStyle("-fx-background-color:transparent;");
			okButton.setOnMouseEntered(h -> {
				okButton.setStyle("-fx-background-color:#196CC0;");
			});
			okButton.setOnMouseExited(h -> {
				okButton.setStyle("-fx-background-color:transparent;");
			});
			okButton.setOnAction(g -> {
				if (b1.isSelected()) {
					table.getItems().removeAll(table.getSelectionModel().getSelectedItems());
				} else if (b2.isSelected()) {
					table.getItems().clear();
				}
				deleteStage.close();
			});
//			okButton.setOnAction(f->{
//				
//			});
			HBox box = new HBox(10);
			box.getChildren().addAll(okButton);
			box.setAlignment(Pos.CENTER_RIGHT);

			deleteBox.getChildren().addAll(b1, b2, box);
			Scene deleteScene = new Scene(deleteBox, 300, 150);
			deleteStage.setScene(deleteScene);
			deleteStage.setTitle("Supprimer");
			deleteStage.initOwner(primaryStage);
			deleteStage.show();
			deleteStage.setX(deleteButton.getLayoutX() + primaryStage.getX() + 30);
			deleteStage.setY(bottom.getLayoutY() + primaryStage.getY());
			int[] p = { 10 };
			Timeline line = new Timeline(new KeyFrame(Duration.millis(20), f -> {
				deleteStage.setY(bottom.getLayoutY() + primaryStage.getY() - p[0]);
				p[0] = p[0] + 10;
			}));
			line.setCycleCount(15);
			line.play();
			deleteStage.focusedProperty().addListener((v, o, n) -> {
				if (Boolean.FALSE.equals(n)) {
					Timeline timeline1 = new Timeline();
					timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(40), ev -> {
						deleteStage.setOpacity(deleteStage.getOpacity() / 1.3);
					}));
					timeline1.setCycleCount(Animation.INDEFINITE);
					timeline1.play();
					if (deleteStage.getOpacity() < 1 / 64)
						deleteStage.hide();
				}
			});
//			TranslateTransition translate = new TranslateTransition(Duration.seconds(3),deleteBox);
//			translate.setByY(-150);
//			translate.setCycleCount(1);
//			translate.play();
//			Alert al = new Alert(AlertType.WARNING);
//			al.setHeaderText(deleteButton.getLayoutX()+"");
//			al.setContentText(bottom.getLayoutY()+"");
//			al.showAndWait();
		});
		
		
		
		
		
		
		

		Button addButton = new Button("Ajouter un dossier");
		addButton.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Choisir un répertoire");
			File selectedDirectory = directoryChooser.showDialog(primaryStage);
			if (selectedDirectory != null) {
				System.out.println(liste.size());
				System.out.println("Répertoire sélectionné : " + selectedDirectory.getAbsolutePath());
				
				Task<Void> copyTask = new Task<Void>() {
					
					
					/*
					 * 
					*/ 
					
					
					@Override
					protected Void call() throws Exception {
						Platform.runLater(()->{
							progress.setProgress(0);
						});
						getVideos(selectedDirectory.getAbsolutePath(),tableView.getSelectionModel().getSelectedItem());
						progress.setVisible(false);
						rechercheLabel.setVisible(false);
						trier();
						return null;
					}
				};
				
//				progress.progressProperty().bind(copyTask.progressProperty());
				
				
				new Thread(copyTask).start();
				System.out.println("J'ai fini");
			} else {
				System.out.println("Aucun répertoire sélectionné !");
			}
		});

		Button deleteAllButton = new Button("Vider la liste de lecture");
		deleteAllButton.setOnAction(e -> {
			liste.clear();
		});
		liste.addListener(new ListChangeListener<ModeleVideo>() {

			@Override
			public void onChanged(Change<? extends ModeleVideo> c) {
				if (liste.isEmpty()) {
					motifTri.setDisable(true);
					ordreTri.setDisable(true);
				} else {
					motifTri.setDisable(false);
					ordreTri.setDisable(false);
				}
			}
		});

		
		
		
		table.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
		table.setOnDragDropped(event->{
		    boolean success = false;
		    if (event.getDragboard().hasFiles()) {
			    List<File> files = event.getDragboard().getFiles();
			    for(File f:files) {
			    	getVideos(f.getAbsolutePath(), tableView.getSelectionModel().getSelectedItem());
			    }
			    Platform.runLater(()->{
			    	progress.setVisible(false);
					rechercheLabel.setVisible(false);
					trier();
			    });
			    success = true;
		     }
		            event.setDropCompleted(success);
		            event.consume();
		});

//        table.setOnDragDropped(event -> {
//            boolean success = false;
//            if (event.getDragboard().hasFiles()) {
//                List<File> files = event.getDragboard().getFiles();
//                addFilesToList(files);
//                success = true;
//            }
//            event.setDropCompleted(success);
//            event.consume();
//        });
		
		
		
	
		
		rechercheLabel.setStyle("-fx-text-fill:white;-fx-font-size:18px");
		
//		
//		progress.progressProperty().addListener((v,o,n)->{
//			if(n.doubleValue() == 1) {
//				progress.setVisible(false);
//				rechercheLabel.setVisible(false);
//			}else {
//				progress.setVisible(true);
//				rechercheLabel.setVisible(true);
//			}
//		});
		
		
		
		bottom.getChildren().addAll(rechercheLabel,progress,deleteButton, deleteAllButton, addButton);
		bottom.setAlignment(Pos.CENTER_RIGHT);
		bottom.setStyle("-fx-background-color:#340641;-fx-padding:10px");

//		BorderPane.setMargin(bottom, new Insets(10, 10, 10, 0));
		root.setBottom(bottom);
//		root.setBottom(menuBar);

		scene.getStylesheets().add(getClass().getResource("Main.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Music Player");
		primaryStage.getIcons().add(new Image(getClass().getResource("favicon.png").toString()));
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitHint("");
		primaryStage.setFullScreen(false);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(e->{
			try {
				String hj = "listeDeLecture.prince";
//				FileOutputStream fis = new FileOutputStream(System.getProperty("user.home")+"\\Videos\\fichier.data");
//				Alert gd = new Alert(AlertType.INFORMATION);
//				gd.setHeaderText(hj);
//				gd.setContentText(hj);
//				gd.showAndWait();
				FileOutputStream fis = new FileOutputStream(hj, false);
				ObjectOutputStream oos = new ObjectOutputStream(fis);
				for(Liste l:tableView.getItems()) {
					oos.writeObject(l);
				}
				oos.close();
				fis.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
	}

//	private void copy(ObservableList<ModeleVideo> listefiltre, ObservableList<ModeleVideo> liste) {
//		int i = 0;
//		for (ModeleVideo video : listefiltre) {
//			liste.set(i++, video);
//		}
//		liste.remove(i, liste.size());
//	}

	public void trier() {
		String ordre = ordreTri.getValue();
		String motif = motifTri.getValue();
		if (ordre != null && motif != null) {
			if (motif.equals("Nom")) {
				if (ordreListe.get(0).equals(ordre)) {
					liste.sort(Comparator.comparing(ModeleVideo::getNom));
				} else {
					liste.sort(Comparator.comparing(ModeleVideo::getNom).reversed());
				}
			} else if (motif.equals("Date")) {
				if (ordreListe.get(0).equals(ordre)) {
					liste.sort(Comparator.comparing(ModeleVideo::getDate));
				} else {
					liste.sort(Comparator.comparing(ModeleVideo::getDate).reversed());
				}
			} else {
				if (ordreListe.get(0).equals(ordre)) {
					liste.sort(Comparator.comparing(ModeleVideo::getTaille));
				} else {
					liste.sort(Comparator.comparing(ModeleVideo::getTaille).reversed());
				}
			}
		}

	}
	public void getVideos(String chemin,Liste conteneur) {
		Platform.runLater(()->{
			progress.setVisible(true);
			rechercheLabel.setVisible(true);
		});
		progress.setProgress(0);
		File file = new File(chemin);
//		Platform.runLater(()->{
//			Alert g = new Alert(AlertType.INFORMATION);
//			g.setHeaderText(file.listFiles().length+"");
//			g.showAndWait();
//		});
		if(file.listFiles() == null) {
			getVideos(chemin,conteneur,1);
		}else {
			getVideos(chemin,conteneur,file.listFiles().length);
		}
		
	}
	
	public void getVideos(String chemin,Liste conteneur,double taille) {
		File file = new File(chemin);
		if (file.isDirectory() && file.listFiles() != null) {
			for (File fichier : file.listFiles()) {
				if (fichier.isDirectory()) {
					getVideos(fichier.getAbsolutePath(),conteneur,fichier.listFiles().length*taille);
				} else {
					if (fichier.getName().endsWith(".mp4") || fichier.getName().endsWith(".mp3")) {
						try {
								Media m = new Media(fichier.toURI().toString());
							Platform.runLater(() -> {
	
								Media me = new Media(fichier.toURI().toString());
								
//								tableView.getSelectionModel().getSelectedItem().liste.add(new ModeleVideo(fichier.getName(), fichier.getAbsolutePath(),
//										new SimpleDateFormat("dd/MM/yyyy").format(new Date(fichier.lastModified())),
//										(int) fichier.length(), m.getWidth(), m.getHeight(),
//										new SimpleDateFormat("HH:mm:ss")
//												.format(new Date((long) (me.getDuration().toMillis() - 3600 * 1000)))));
								
								  
								
								
//								
								if(!tableView.getSelectionModel().getSelectedItem().equals(conteneur)) {
									conteneur.liste.add(new ModeleVideo(fichier.getName(), fichier.getAbsolutePath(),
											new SimpleDateFormat("dd/MM/yyyy").format(new Date(fichier.lastModified())),
											(int) fichier.length(), m.getWidth(), m.getHeight(),
											new SimpleDateFormat("HH:mm:ss")
													.format(new Date((long) (me.getDuration().toMillis() - 3600 * 1000)))));
								}else {
									liste.add(new ModeleVideo(fichier.getName(), fichier.getAbsolutePath(),
										new SimpleDateFormat("dd/MM/yyyy").format(new Date(fichier.lastModified())),
										(int) fichier.length(), m.getWidth(), m.getHeight(),
										new SimpleDateFormat("HH:mm:ss")
												.format(new Date((long) (me.getDuration().toMillis() - 3600 * 1000)))));
								}
																
							
								
							});
						}catch(Exception d) {
							
						}
					}
					Platform.runLater(()->{
						progress.setProgress(progress.getProgress()+1.0/taille);
//						Alert g = new Alert(AlertType.INFORMATION);
//						g.setHeaderText(progress.getProgress()+" ");
//						g.setContentText(taille+"");
//						g.showAndWait();
						
					});
				}
				
				
			}
		} else {
			if (file.isDirectory()) {
				getVideos(file.getAbsolutePath(),conteneur);
			} else {
				if (file.getName().endsWith(".mp4") || file.getName().endsWith(".mp3")) {
					liste.add(new ModeleVideo(file.getName(), file.getAbsolutePath(),
							new SimpleDateFormat("dd/MM/yyyy").format(new Date(file.lastModified())),
							(int) file.length(), 0, 0, "20"));
				}
			}
		}

//		Alert o = new Alert(AlertType.INFORMATION);
//		o.setHeaderText(motifTri.getValue()== null ? "65":motifTri.getValue());
//		o.showAndWait();
//		
//		if(motifTri.getValue() != null) {
//			
//			
//			
//			if (motifTri.getValue().equals("Date")) {
//				ordreListe.setAll("Plus récent en premier", "Plus récent en dernier");
//			} else if (motifTri.getValue().equals("Taille")) {
//				ordreListe.setAll("Plus volumineux en premier", "Plus volumineux en dernier");
//			} else {
//				ordreListe.setAll("Ordre croissant", "Ordre décroissant");
//			}
//			
//			ordreTri.setValue(ordreListe.get(0));
//
//			String ordre =ordreTri.getValue();
//
//			String motif = motifTri.getValue();
//			if (motif.equals("Nom")) {
//				if (ordreListe.get(0).equals(ordre)) {
//					liste.sort(Comparator.comparing(ModeleVideo::getNom));
//				} else {
//					liste.sort(Comparator.comparing(ModeleVideo::getNom).reversed());
//				}
//			} else if (motif.equals("Date")) {
//				if (ordreListe.get(0).equals(ordre)) {
//					liste.sort(Comparator.comparing(ModeleVideo::getDate));
//				} else {
//					liste.sort(Comparator.comparing(ModeleVideo::getDate).reversed());
//				}
//			} else {
//				if (ordreListe.get(0).equals(ordre)) {
//					liste.sort(Comparator.comparing(ModeleVideo::getTaille));
//				} else {
//					liste.sort(Comparator.comparing(ModeleVideo::getTaille).reversed());
//				}
//			}
//		}
//		

	}

	private static void pause(KeyCode f) {
		if (f == KeyCode.SPACE) {
			if (!player[0].getStatus().equals(MediaPlayer.Status.PAUSED)) {
				player[0].pause();
			} else {
				player[0].play();
			}
		}
	}

	public static boolean playVideo(Image pauseImage, Image continueImage, Image backImage, Image previous, Image next,
			Image icon, int numero, boolean lireTout1) {

//		IntegerProperty pause = new SimpleIntegerProperty(0);
		
		if(fen.isShowing() || stage.isShowing())
			player[0].dispose();
		
		playPane.getChildren().clear();

		if (table.getItems().isEmpty())
			return true;
		scene1.getStylesheets().add(cheminBase.getResource("Video.css").toString());
		BooleanProperty bool = new SimpleBooleanProperty(lireTout1);// Pour la lecture automatique
		BooleanProperty boucle = new SimpleBooleanProperty(false);// Pour la lecture automatique
		boolean[] suivant = { false };
		
		
		
		Bouton bouton1 = new Bouton("bouton1");

		Bouton bouton2 = new Bouton("bouton2");

		HBox hbox1 = new HBox(1);
		hbox1.getChildren().addAll(bouton1, bouton2);
		hbox1.setAlignment(Pos.BASELINE_LEFT);

		Bouton bouton3 = new Bouton("bouton3");

		bouton3.setOnAction(e -> {
			player[0].seek(player[0].getCurrentTime().add(Duration.seconds(-10)));
		});
		bouton3.setOnKeyPressed(e -> {
			pause(e.getCode());
			if (e.getCode().equals(KeyCode.ENTER))
				player[0].seek(player[0].getCurrentTime().add(Duration.seconds(-10)));
		});

		Button bouton4 = new Button("",  new ImageView(new Image(cheminBase.getResource("pause (2).png").toString(), 30, 30, true, true)));
//		
		bouton4.setBorder(new Border(new BorderStroke(Color.DIMGREY, BorderStrokeStyle.SOLID, new CornerRadii(100),null)));
		bouton4.setId("pause");
		bouton4.focusedProperty().addListener((f,g,h)->{
			if(h) {
				bouton4.setBorder(new Border(new BorderStroke(Color.DARKGOLDENROD, BorderStrokeStyle.SOLID, new CornerRadii(100),null)));
			}else {
				bouton4.setBorder(new Border(new BorderStroke(Color.DIMGREY, BorderStrokeStyle.SOLID, new CornerRadii(100),null)));
			}
		});
		bouton4.setTranslateY(-5);
//		
//		bouton4.setStyle("-fx-background-color:transparent");
		
		

		bouton4.setOnAction(e -> {
			if (!player[0].getStatus().equals(MediaPlayer.Status.PAUSED)) {
				player[0].pause();
			}else {
				player[0].play();
			}
				
		});
		Bouton bouton5 = new Bouton("bouton5");

		bouton5.setOnMouseClicked(e -> {
			player[0].seek(player[0].getCurrentTime().add(Duration.seconds(10)));
		});
		bouton5.setOnKeyPressed(e -> {
			pause(e.getCode());
			if (e.getCode().equals(KeyCode.ENTER))
				player[0].seek(player[0].getCurrentTime().add(Duration.seconds(10)));
		});

		HBox hbox2 = new HBox(1);
		hbox2.getChildren().addAll(bouton3, bouton4, bouton5);
		hbox2.setAlignment(Pos.BASELINE_CENTER);

		Bouton bouton6 = new Bouton("bouton6");

//		,new ImageView( new Image( cheminBase.getResource("plein.png").toString(),30,30,true,false))

		Bouton bouton7 = new Bouton("",
				new ImageView(new Image(cheminBase.getResource("plein.png").toString(), 20, 20, true, false)));
		bouton7.setOnMouseClicked(e -> {
			stage.setFullScreen(!stage.isFullScreen());
		});
		bouton7.setOnKeyPressed(e -> {
			pause(e.getCode());
			if (e.getCode().equals(KeyCode.ENTER))
				stage.setFullScreen(!stage.isFullScreen());
		});
//		bouton7.setFocusTraversable(true);

//		playPane.setOnKeyPressed(event -> {
//			if (event.getCode() == KeyCode.SPACE) {
//				if (!player[0].getStatus().equals(MediaPlayer.Status.PAUSED)) {
//					player[0].pause();
//				} else {
//					player[0].play();
//				}
//			}
//		});		

		stage.fullScreenProperty().addListener((v, o, n) -> {
			if (n) {
				bouton7.setGraphic(
						new ImageView(new Image(cheminBase.getResource("exit.png").toString(), 20, 20, true, false)));
			} else {
				bouton7.setGraphic(
						new ImageView(new Image(cheminBase.getResource("plein.png").toString(), 20, 20, true, false)));
			}
		});
		bouton7.setTooltip(new Tooltip("Plein écran"));

		Bouton bouton8 = new Bouton("bouton8");

		bouton8.setTooltip(new Tooltip("Lire en miniature"));
		Bouton bouton9 = new Bouton("",
				new ImageView(new Image(cheminBase.getResource("more.png").toString(), 30, 30, true, false)));

		HBox hbox3 = new HBox(1);
		
		BooleanProperty aleatoire = new SimpleBooleanProperty(false);
		
		
		
		
		CheckBox button2 = new CheckBox("Lecture en Aleatoire");
		button2.setPadding(new Insets(3));
		
		CheckBox button3 = new CheckBox("Lecture en boucle");
		button3.setPadding(new Insets(3));
		button3.setSelected(boucle.getValue());
		boucle.addListener((j, k, l) -> {
			button3.setSelected(l);
		});
		button3.setOnMouseClicked(f -> {
			boucle.set(boucle.not().getValue());
		});

		CheckBox button4 = new CheckBox("Lecture automatique");
		button4.setPadding(new Insets(3));
		button4.setSelected(bool.getValue());
		bool.addListener((j, k, l) -> {
			button4.setSelected(l);
		});
		button4.setOnMouseClicked(f -> {
			bool.set(bool.not().getValue());
		});
		

		VBox box = new VBox(1);
		box.setPadding(new Insets(10));
		
		button2.disableProperty().bind(button3.selectedProperty().or(button4.selectedProperty()));
		button3.disableProperty().bind(button2.selectedProperty().or(button4.selectedProperty()));
		button4.disableProperty().bind(button2.selectedProperty().or(button3.selectedProperty()));
		
		
		
		
		box.getChildren().addAll(button2, button3, button4);

		Scene sceneOptions = new Scene(box);

		
		op.setScene(sceneOptions);
		
		
		bouton9.setOnKeyPressed(e->{
			if(e.getCode().equals(KeyCode.ENTER)) {
				aleatoire.bind(button2.selectedProperty());
				
				op.show();
				op.setOpacity(0);
				op.setX(bouton9.getLayoutX() + hbox3.getLayoutX() + stage.getX() - op.getHeight() + 30);
				op.setY(stage.getHeight() + stage.getY() - 2 * op.getHeight() - hbox3.getHeight() - 40);

				Timeline line = new Timeline(new KeyFrame(new Duration(30), ev -> {
					op.setOpacity(op.getOpacity() + 0.2);
					op.setY(op.getY() + 30);
				}));

				line.setCycleCount(5);
				line.play();
				
				op.focusedProperty().addListener((v, o, n) -> {
					if (Boolean.FALSE.equals(n))
						op.hide();
				});
			}else if(e.getCode().equals(KeyCode.SPACE)) {
				if(player[0].getStatus().equals(MediaPlayer.Status.PLAYING))
					player[0].pause();
				else {
					player[0].play();
				}
			}
		});
		
		
		bouton9.setOnMouseClicked(e -> {
		
			
		
			
			aleatoire.bind(button2.selectedProperty());
			
			op.show();
			op.setOpacity(0);
			op.setX(bouton9.getLayoutX() + hbox3.getLayoutX() + stage.getX() - op.getHeight() + 30);
			op.setY(stage.getHeight() + stage.getY() - 2 * op.getHeight() - hbox3.getHeight() - 40);

			Timeline line = new Timeline(new KeyFrame(new Duration(30), ev -> {
				op.setOpacity(op.getOpacity() + 0.2);
				op.setY(op.getY() + 30);
			}));

			line.setCycleCount(5);
			line.play();

			op.focusedProperty().addListener((v, o, n) -> {
				if (Boolean.FALSE.equals(n))
					op.hide();
			});

		});

		hbox3.getChildren().addAll(bouton7, bouton6, bouton8, bouton9);
		hbox3.setAlignment(Pos.BASELINE_RIGHT);

		HBox footer2 = new HBox(hbox1, hbox2, hbox3);
		footer2.setMaxHeight(30);

		HBox.setHgrow(hbox1, Priority.ALWAYS);
		HBox.setHgrow(hbox2, Priority.ALWAYS);
		HBox.setHgrow(hbox3, Priority.ALWAYS);

		IntegerProperty num = new SimpleIntegerProperty(1);

		/* Début Les boutons de suivant et précédent */
		ImageView nextImageView = new ImageView(next);

		nextImageView.setOnMouseClicked(e -> {
			player[0].stop();
			num.set((num.intValue() + 1) % (liste.size()));
		});
		nextImageView.setOnMouseClicked(e -> {

		});

		Bouton nextButton = new Bouton();
		nextButton.setTooltip(new Tooltip("Vidéo suivante"));
		nextButton.setGraphic(nextImageView);
		nextButton.setOnKeyPressed(e -> {
			pause(e.getCode());
			if (e.getCode().equals(KeyCode.ENTER)) {
				player[0].stop();
				num.set((num.intValue() + 1) % (liste.size()));
			}
		});
//		nextButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
//		nextButton.setTooltip(new Tooltip("Vidéo suivante"));
		nextButton.setOnMouseClicked(e -> {
			player[0].stop();
			num.set((num.intValue() + 1) % (liste.size()));
		});
		nextButton.setPrefSize(30, 30);
		nextButton.translateXProperty().bind(stage.widthProperty().divide(2).add(-20));
//		Alert fadel = new Alert(AlertType.INFORMATION);
//		fadel.setHeaderText(stage.getWidth()+"     "+scene1.getWidth());
//		fadel.showAndWait();

		ImageView previousImageView = new ImageView(previous);

		previousImageView.setOnMouseClicked(e -> {
			player[0].stop();
			num.set(num.intValue() > 0 ? (num.intValue() - 1) % (liste.size()) : liste.size() - 1);
		});

		Bouton previousButton = new Bouton();
		previousButton.setOnKeyPressed(e -> {
			pause(e.getCode());
			if (e.getCode().equals(KeyCode.ENTER)) {
				player[0].stop();
				num.set(num.intValue() > 0 ? (num.intValue() - 1) % (liste.size()) : liste.size() - 1);
			}
		});
		previousButton.setGraphic(previousImageView);
//		previousButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		previousButton.setOnMouseClicked(e -> {
			player[0].stop();
			num.set(num.intValue() > 0 ? (num.intValue() - 1) % (liste.size()) : liste.size() - 1);
		});
		previousButton.setPrefSize(30, 30);
		previousButton.translateXProperty().bind(stage.widthProperty().divide(-2).add(20));

//		previousButton.setTranslateX(-scene1.widthProperty().get()/2+20);

		/* Fin Les boutons de suivant et précédent */

		Slider progressSlider = new Slider();
		progressSlider.setMin(0);
		progressSlider.setMax(1);
		progressSlider.setValue(0);
		progressSlider.setBlockIncrement(0.05);
		progressSlider.setFocusTraversable(false);
		
		StackPane sliderPane = new StackPane(progressSlider);

		Text gauche = new Text();
		gauche.setFill(Color.web("#124578"));
		gauche.setFont(Font.font("Arial", FontWeight.BLACK, 15));
		Text droite = new Text();
		droite.setFill(Color.web("#124578"));
		droite.setFont(Font.font("Arial", FontWeight.BLACK, 15));

		HBox durationBox = new HBox();
		durationBox.setPrefSize(800, 30);
		HBox b1 = new HBox(droite);
		durationBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		b1.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		b1.setAlignment(Pos.CENTER_RIGHT);
		durationBox.getChildren().addAll(gauche, b1);
		HBox.setHgrow(b1, Priority.ALWAYS);

		VBox footer1 = new VBox(5);
		footer1.getChildren().addAll(sliderPane, durationBox, footer2);
		footer1.setPadding(new Insets(0, 15, 0, 15));
		VBox.setMargin(footer2, new Insets(-15, 0, 0, 0));

		player[0] = new MediaPlayer(new Media(new File(liste.get(0).getChemin()).toURI().toString()));

		ModeleVideo[] modele = { table.getItems().get(0) };

		MediaView[] view = { new MediaView() };
		view[0].maxWidth(Double.MAX_VALUE);
		view[0].maxWidth(Double.MAX_VALUE);

//		Stage fen = new Stage();
//		fen.initStyle(StageStyle.UTILITY);
//		fen.setAlwaysOnTop(true);
//		fen.setResizable(true);

		HBox fenBox = new HBox(2);

		Button fenButton1 = new Button("bouton1");
//		fenButton1.setStyle("-fx-background-color:yellow");
		Button fenButton2 = new Button("bouton2");
//		fenButton2.setStyle("-fx-background-color:yellow");
		fenButton2.setOnAction(e -> {
			if (!player[0].getStatus().equals(MediaPlayer.Status.PAUSED))
				player[0].pause();
			else
				player[0].play();
		});
		Button fenButton3 = new Button("bouton3");
//		fenButton3.setStyle("-fx-background-color:yellow");

		fenButton1.setOnAction(e -> {
			player[0].seek(player[0].getCurrentTime().add(Duration.seconds(-10)));
		});
		fenButton3.setOnAction(e -> {
			player[0].seek(player[0].getCurrentTime().add(Duration.seconds(10)));
		});

		Bouton vue = new Bouton("Bouton");
//		fenBox.translateXProperty().bind(fen.widthProperty().divide(2).add(-80));
//		fenBox.translateYProperty().bind(fen.heightProperty().divide(2).add(-30));

		StackPane vueBox = new StackPane();
//		vueBox.setAlignment(Pos.BOTTOM_RIGHT);
//		vueBox.setMaxSize(80,40);
//		vueBox.setStyle("-fx-background-color:green");
//		vueBox.getChildren().addAll(fenBox,vue);
//		
//		StackPane.setAlignment(vue,Pos.TOP_CENTER);
//		
//		StackPane.setAlignment(fenBox,Pos.CENTER);

		HBox pm = new HBox(vue);
		pm.setMaxHeight(30);
		fenBox.setMaxHeight(30);
		pm.maxWidthProperty().bind(fen.widthProperty().add(0));
		fenBox.maxWidthProperty().bind(fen.widthProperty().add(0));
		pm.setStyle("-fx-background-color:green");
		fenBox.setStyle("-fx-background-color:fff");
		pm.setAlignment(Pos.CENTER_RIGHT);

		vueBox.getChildren().addAll(fenBox, pm);
		StackPane.setAlignment(pm, Pos.BOTTOM_CENTER);
		StackPane.setAlignment(fenBox, Pos.CENTER);
		fenBox.setAlignment(Pos.CENTER);

		fenBox.getChildren().addAll(fenButton1, fenButton2, fenButton3);

		bouton8.setOnAction(e -> {
			stage.hide();
			fen.setScene(stage.getScene());
			fen.show();
			fen.setMaxWidth(500);
			fen.setMaxHeight(310);
			fen.setX(Screen.getPrimary().getVisualBounds().getWidth() - 500);
			fen.setY(Screen.getPrimary().getVisualBounds().getHeight() - 350);

			footer1.setVisible(false);

			if(!playPane.getChildren().contains(vueBox))
				playPane.getChildren().add(vueBox);

//			playPane.getChildren().add(fenBox);

//			vue.translateXProperty().bind(scene1.widthProperty().divide(3).add(0));
//			vue.translateYProperty().bind(scene1.heightProperty().divide(3.5));

//			StackPane.setAlignment(vueBox, Pos.BOTTOM_RIGHT);

//			vueBox.setOnMouseClicked(f->{
//				
//				Alert h = new Alert(AlertType.INFORMATION);
//				h.showAndWait();
//				
//				fen.close();
//				stage.setScene(fen.getScene());
////				playPane.getChildren().remove(fenBox);
//				playPane.getChildren().remove(vueBox);
////				playPane.getChildren().add(footer1);
////				playPane.getChildren().remove(fenBox);
////				fenBox.setVisible(false);
////				vue.setVisible(false);
//				footer1.setVisible(true);
////				playPane.getChildren().remove(vue);
//				stage.show();
//				stage.setFullScreen(true);
//			});

//			vue.setOnMouseEntered(f -> {
//				fen.close();
//				stage.setScene(fen.getScene());
//				playPane.getChildren().remove(fenBox);
//				playPane.getChildren().remove(vueBox);
////				playPane.getChildren().add(footer1);
////				playPane.getChildren().remove(fenBox);
////				fenBox.setVisible(false);
////				vue.setVisible(false);
//				footer1.setVisible(true);
////				playPane.getChildren().remove(vue);
//				stage.show();
//				stage.setFullScreen(true);
//			});

//			vue.setOnAction(f -> {
//				fen.close();
//				stage.setScene(fen.getScene());
//				playPane.getChildren().remove(vueBox);
////				playPane.getChildren().add(footer1);
////				playPane.getChildren().remove(fenBox);
////				fenBox.setVisible(false);
////				vue.setVisible(false);
//				footer1.setVisible(true);
////				playPane.getChildren().remove(vue);
//				stage.show();
//				stage.setFullScreen(true);
//			});
//			fen.setOnCloseRequest(f -> {
//				player[0].stop();
//				player[0].dispose();
//				playPane = new StackPane();
//				scene1 = new Scene(playPane, Screen.getPrimary().getVisualBounds().getWidth(),
//						Screen.getPrimary().getVisualBounds().getHeight() - 30);
//				stage = new Stage();
//			});
			playPane.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.SPACE) {
					if (player[0].getStatus().equals(MediaPlayer.Status.PAUSED))
						player[0].play();
					else
						player[0].pause();
				} else if (event.getCode() == KeyCode.RIGHT) {
					player[0].seek(player[0].getCurrentTime().add(Duration.seconds(10)));
				} else if (event.getCode() == KeyCode.LEFT) {
					player[0].seek(player[0].getCurrentTime().add(Duration.seconds(-10)));
				}
			});

		});

		playPane.getChildren().add(view[0]);
		footer1.translateYProperty().bind(scene1.heightProperty().add(-90));
		playPane.getChildren().add(previousButton);
		playPane.getChildren().add(nextButton);
		playPane.getChildren().add(footer1);
		playPane.setStyle("-fx-background-color:#fff");

		progressSlider.setOnMouseClicked(event -> {
			Duration duration = player[0].getMedia().getDuration();
			double newPosition = event.getX() / progressSlider.getWidth();
			player[0].seek(duration.multiply(newPosition));
		});

		progressSlider.setOnMouseDragged(event -> {
			Duration duration = player[0].getMedia().getDuration();
			double newPosition = event.getX() / progressSlider.getWidth();
			player[0].seek(duration.multiply(newPosition));
		});

		progressSlider.setOnMouseDragEntered(event -> {
			Duration duration = player[0].getMedia().getDuration();
			double newPosition = event.getX() / progressSlider.getWidth();
			player[0].seek(duration.multiply(newPosition));
		});
		progressSlider.setOnMousePressed(e -> {
			Duration duration = player[0].getMedia().getDuration();
			double newPosition = e.getX() / progressSlider.getWidth();
			player[0].seek(duration.multiply(newPosition));
		});
		progressSlider.setOnMouseDragOver(e -> {
			Duration duration = player[0].getMedia().getDuration();
			double newPosition = e.getX() / progressSlider.getWidth();
			player[0].seek(duration.multiply(newPosition));
		});
		progressSlider.setOnMouseDragReleased(event -> {
			Duration duration = player[0].getMedia().getDuration();
			double newPosition = event.getX() / progressSlider.getWidth();
			player[0].seek(duration.multiply(newPosition));
		});
		progressSlider.setOnMouseDragExited(e -> {
			Duration duration = player[0].getMedia().getDuration();
			double newPosition = e.getX() / progressSlider.getWidth();
			player[0].seek(duration.multiply(newPosition));
		});

		stage.getIcons().add(icon);

		
		
		Slider volumeSlider = new Slider(0, 1, 0.5);
		volumeSlider.setPrefWidth(150);
		volumeSlider.setTooltip(new Tooltip("Ajuster le volume"));
		
		Text volumeText = new Text("50");
		volumeText.prefWidth(30);
		volumeText.setStroke(Color.CHOCOLATE);
		volumeSlider.valueProperty().addListener((ob, ol, nl) -> {
			player[0].setVolume(nl.doubleValue());
			volumeText.setText(String.valueOf((int) (100*nl.doubleValue())));
		});
		
		Bouton volumeButton = new Bouton("", new ImageView(new Image(cheminBase.getResource("volume.png").toString(), 20, 20, true, true)));
		
//		ImageView volumeView = new ImageView(new Image(cheminBase.getResource("volume.png").toString(), 20, 20, true, true));
		volumeButton.setTranslateY(-10);
		volumeButton.setOnAction(ie->{
			if(player[0].isMute()) {
				player[0].setMute(false);
				volumeButton.setGraphic(new ImageView(new Image(cheminBase.getResource("volume.png").toString(), 20, 20, true, true)));
			}else {
				player[0].setMute(true);
				volumeButton.setGraphic(new ImageView(new Image(cheminBase.getResource("mute.png").toString(), 20, 20, true, true)));
			}
			
		});
		
		HBox volumeBox = new HBox(2);
		volumeBox.getChildren().addAll(volumeButton,volumeSlider,volumeText);
		sliderPane.getChildren().add(volumeBox);
		volumeBox.setVisible(false);
		volumeSlider.focusedProperty().addListener((vc,oc,nc)->{
//			if(!nc)
				
		});
		playPane.setOnMouseClicked(f->{
			Timeline line = new Timeline(new KeyFrame(new Duration(5), g->{
					volumeBox.setTranslateY(volumeBox.getTranslateY()+1);
				}));
				line.setCycleCount(45);
				line.play();
				line.setOnFinished(g->{
					volumeBox.setVisible(false);
				});	
		});
		
		
		bouton1.setOnAction(f->{
//			if(!sliderPane.getChildren().contains(volumeBox))
			if(!volumeBox.isVisible()) {
				volumeBox.setTranslateY(10);
				volumeBox.setVisible(true);
				Timeline line = new Timeline(new KeyFrame(new Duration(5), g->{
					volumeBox.setTranslateY(volumeBox.getTranslateY()-1);
				}));
				line.setCycleCount(35);
				line.play();
			}else {
				Timeline line = new Timeline(new KeyFrame(new Duration(5), g->{
					volumeBox.setTranslateY(volumeBox.getTranslateY()+1);
				}));
				line.setCycleCount(45);
				line.play();
				line.setOnFinished(g->{
					volumeBox.setVisible(false);
				});	
			}
			
		});
		
		
		

		ImageView pauseContinueView = new ImageView();
		pauseContinueView.setImage(pauseImage);
		pauseContinueView.setSmooth(true);

		playPane.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.SPACE) {
				if (pauseContinueView.getImage().equals(pauseImage)) {
					pauseContinueView.setImage(continueImage);
					player[0].pause();
				} else {
					pauseContinueView.setImage(pauseImage);
					player[0].play();
				}
			} else if (event.getCode() == KeyCode.RIGHT) {
				player[0].seek(player[0].getCurrentTime().add(Duration.seconds(10)));
			} else if (event.getCode() == KeyCode.LEFT) {
				player[0].seek(player[0].getCurrentTime().add(Duration.seconds(-10)));
			}
		});

		progressSlider.getStyleClass().add("slider");

		view[0].fitWidthProperty().bind(playPane.widthProperty());
		view[0].fitHeightProperty().bind(playPane.heightProperty());

		view[0].setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				if (pauseContinueView.getImage().equals(pauseImage)) {
					pauseContinueView.setImage(continueImage);
					player[0].pause();
				} else {
					pauseContinueView.setImage(pauseImage);
					player[0].play();
				}
			}
		});

		pauseContinueView.setOnMouseClicked(ev -> {
			if (pauseContinueView.getImage().equals(pauseImage)) {
				pauseContinueView.setImage(continueImage);
				player[0].pause();
			} else {
				pauseContinueView.setImage(pauseImage);
				player[0].play();
			}
		});

		suivant[0] = true;
		num.addListener((v, o, n) -> {

			if (Boolean.TRUE.equals(suivant[0])) {
				
				
				
				
				

				modele[0] = table.getItems().get(num.intValue());
				File video = new File(modele[0].getChemin());
				player[0] = new MediaPlayer(new Media(video.toURI().toString()));
				view[0].setMediaPlayer(player[0]);
//			playPane.relocate(1, 1);

				progressSlider.setStyle(
						"-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;"
								+ "-fx-background-insets: 0, 1, 2, 3;" + "-fx-background-radius: 4;");

//			progressSlider.setStyle(".slider {\n"
//				    + "    -fx-background-color: transparent;-fx-background-radius: 4;-fx-background-insets: 0, 1, 2, 3;\n"
//				    + "}\n"
//				    + "\n"
//				    + ".progress-bar .track {\n"
//				    + "    -fx-background-color: #212121;\n"
//				    + "}\n"
//				    + "\n"
//				    + ".progress-bar .bar {\n"
//				    + "    -fx-background-color: #FF0000;\n"
//				    + "}\n"
//				    + "\n"
//				    + ".progress-bar .thumb {\n"
//				    + "    -fx-background-color: #FF0000;\n"
//				    + "    -fx-background-insets: 0, 1, 2;\n"
//				    + "    -fx-background-radius: 0.0em, 0.0em, 0.0em;\n"
//				    + "    -fx-padding: 0.0em;\n"
//				    + "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 2, 0.0 , 0 , 1 );\n"
//				    + "}\n"
//				    + "\n"
//				    + ".progress-bar .thumb:hover {\n"
//				    + "    -fx-background-color: #FFFFFF;\n"
//				    + "    -fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.6) , 2, 0.0 , 0 , 1 );\n"
//				    + "}\n"
//				    + "\n"
//				    + ".progress-bar .thumb:pressed {\n"
//				    + "    -fx-background-color: #E6E6E6;\n"
//				    + "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 2, 0.0 , 0 , 1 );}"
//				    + ".progress-bar .track-fill {\r\n"
//				    + "    -fx-background-color: blue;\r\n"
//				    + "}"
//				  );

//			progressSlider.setStyle("-fx-background-color: white; -fx-background-radius: 3px; -fx-padding: 3px;");

//			scene1.widthProperty().addListener((vo, oo, no) -> {
//				view[0].setFitWidth(scene1.getWidth());
//				Alert d = new Alert(AlertType.WARNING);
//				d.setHeaderText(view[0].getFitWidth()+"  pane  "+playPane.getWidth());
//				d.showAndWait();
//				nextButton.setTranslateX(no.doubleValue()/2-20);
//				previousButton.setTranslateX(-scene1.widthProperty().get()/2+20);
//			});
//			scene1.heightProperty().addListener((vo, oo, no) -> {
//				view[0].setFitHeight(scene1.getHeight());
//				view[0].fitHeightProperty().set(no.doubleValue());
//				Alert d = new Alert(AlertType.WARNING);
//				d.showAndWait();
//			});

				int index = (num.intValue() + 1) % (liste.size());

				Media p1 = new Media((new File(liste.get(index).getChemin()).toURI().toString()));

//			Image previewImage = p1.getMetadata().get("image") != null ? (Image) p1.getMetadata().get("image") : null;
				Circle circle = new Circle(30, Color.TRANSPARENT);
				circle.setStroke(Color.MAROON);

				MediaView v1 = new MediaView(new MediaPlayer(p1));
				v1.setFitWidth(60);
				v1.setFitHeight(60);

//			p1.setOnReady(() -> {
//				apercu.setStyle("-fx-background-radius:10000000px;-fx-overflow:auto;");
//				apercu.setPrefSize(60, 60);
//				v1.setFitWidth(50);
//				v1.setFitHeight(50);
//				Circle cercle = new Circle(30);
//				cercle.setFill(Color.TRANSPARENT);
//				cercle.setStroke(Color.MAROON);
//				apercu.getChildren().add(v1);
//				apercu.getChildren().add(cercle);
//				footer1.getChildren().add(apercu);
//
//			});

//			StackPane apercu = new StackPane();
//
//			apercu.getChildren().add(v1);
//			apercu.getChildren().add(circle);
//
//			ComboBox<Double> vitesse = new ComboBox<>();
//			vitesse.getItems().addAll(0.5, 0.75, 1.0, 1.25, 1.5);
//			vitesse.valueProperty().addListener((obs, old, newVal) -> {
//				player[0].setRate(newVal);
//			});
//			vitesse.setPromptText("Changer la vitesse");

				stage.setScene(scene1);

				player[0].setOnError(() -> {
					player[0].stop();
					player[0].dispose();
					suivant[0] = false;
					System.out.println("J'ai rencontré une erreur2");
//					Alert df = new Alert(AlertType.WARNING);
//					df.showAndWait();
					num.set((num.intValue() + 1) % (liste.size()));
				});

				player[0].setOnReady(() -> {
					Duration duration = player[0].getMedia().getDuration();
					double totalSeconds = duration.toSeconds();
					int minutes = (int) (totalSeconds / 60);
					int seconds = (int) (totalSeconds % 60);
					System.out.println("Durée de la vidéo: " + minutes + ":" + seconds);
					player[0].setVolume(1);
					player[0].setRate(1);
					player[0].play();
					stage.setTitle(video.getName());
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					String mot = format.format(new Date((long) duration.toMillis() - 3600 * 1000));
					droite.setText(mot);
					try {
//					if(stage.getStyle().equals(StageStyle.DECORATED)) {
//						stage.initStyle(StageStyle.TRANSPARENT);
//						stage.initStyle(StageStyle.DECORATED);
//					}

						playPane.requestFocus();
						
						if (!fen.isShowing()) {
							try {
								playPane.getChildren().remove(vueBox);
							}catch (Exception e) {
								
							}
							footer1.setVisible(true);
							stage.show();
						} else {
//						playPane.getChildren().add(fenBox);
//						playPane.getChildren().add(vue);
//						playPane.getChildren().add(vueBox);
							fen.setScene(stage.getScene());

							fen.show();
							fen.setMaxWidth(500);
							fen.setMaxHeight(310);
							fen.setX(Screen.getPrimary().getVisualBounds().getWidth() - 500);
							fen.setY(Screen.getPrimary().getVisualBounds().getHeight() - 350);

							if (fen.getWidth() == 500)
								fen.setWidth(499);
							else
								fen.setWidth(500);
							if(!playPane.getChildren().contains(vueBox))
								playPane.getChildren().add(vueBox);
							footer1.setVisible(false);

//						fen.setMaxWidth(500);
//						fen.setMaxHeight(310);
//						StackPane.setAlignment(vue, Pos.CENTER);
//						StackPane.setAlignment(vue, Pos.TOP_CENTER);
//						vue.setTranslateX(-50);
//						vue.setTranslateY(-20);

//						vue.translateXProperty().unbind();
//						vue.translateYProperty().unbind();
//						vue.translateXProperty().bind(fen.widthProperty().divide(3).add(0));
//						vue.translateYProperty().bind(fen.heightProperty().divide(3.5).add(0));
						}

//					stage.setFullScreen(true);
					} catch (Exception e) {
					}
				});

				
				
				
				
				
				
				
				
				
				
				vue.setOnAction(f -> {
					fen.close();
					stage.setScene(fen.getScene());
					playPane.getChildren().remove(vueBox);
//					playPane.getChildren().add(footer1);
//					playPane.getChildren().remove(fenBox);
//					fenBox.setVisible(false);
//					vue.setVisible(false);
					footer1.setVisible(true);
//					playPane.getChildren().remove(vue);
					stage.show();
					stage.setFullScreen(true);
				});
				fen.setOnCloseRequest(f -> {
					player[0].stop();
					player[0].dispose();
					playPane = new StackPane();
					scene1 = new Scene(playPane, Screen.getPrimary().getVisualBounds().getWidth(),
							Screen.getPrimary().getVisualBounds().getHeight() - 30);
					stage = new Stage();
				});
				
				
				
				
				
				
				
				
				
				player[0].currentTimeProperty().addListener((observable, oldValue, newValue) -> {
					Duration duration = player[0].getMedia().getDuration();
					double currentTime = newValue.toMillis() / duration.toMillis();
					progressSlider.setValue(currentTime);
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					String mot = format.format(new Date((long) newValue.toMillis() - 3600 * 1000));
					gauche.setText(mot);

				});
				isVisibleStage.set(false);
				player[0].statusProperty().addListener((obs, oldStatus, newStatus) -> {
					if (newStatus == MediaPlayer.Status.UNKNOWN) {
						System.out.println("La vidéo a rencontré une erreur.");
						player[0].dispose();
						isVisibleStage.set(false);
					} else if (newStatus == MediaPlayer.Status.READY) {
						System.out.println("La vidéo est prête à être lue.");
					} else if (newStatus == MediaPlayer.Status.PLAYING) {
						System.out.println("La vidéo est en train de jouer.");
						bouton4.setGraphic( new ImageView(new Image(cheminBase.getResource("pause (2).png").toString(), 30, 30, true, true)));
						isVisibleStage.set(true);
					} else if (newStatus == MediaPlayer.Status.PAUSED) {
						System.out.println("La vidéo est en pause.");
						bouton4.setGraphic( new ImageView(new Image(cheminBase.getResource("play-button.png").toString(), 30, 30, true, true)));
						isVisibleStage.set(false);
					} else if (newStatus == MediaPlayer.Status.STOPPED) {
						pauseContinueView.setImage(continueImage);
						isVisibleStage.set(false);
					}
				});

				player[0].setOnEndOfMedia(() -> {
					player[0].stop(); 
					if (Boolean.TRUE.equals(aleatoire.getValue())) {
						num.set(((int) (Math.random()*liste.size()+1))%liste.size());
					}else if (Boolean.TRUE.equals(boucle.getValue())) {
						player[0].seek(Duration.ZERO);
						player[0].play();
//						boucle.set(false);
					} else if (Boolean.TRUE.equals(bool.getValue())) {
						suivant[0] = true;
						num.set((num.intValue() + 1) % (liste.size()));
					}else {
						bouton4.setGraphic( new ImageView(new Image(cheminBase.getResource("play-button.png").toString(), 30, 30, true, true)));
					}
//				player[0].seek(Duration.ZERO);
//				System.out.println("J'ai fini ceci");
				});

				bouton6.setOnAction(e -> {
					bool.set(!bool.getValue());
				});

				stage.setOnCloseRequest(event -> {
					player[0].stop();
					player[0].dispose();
					suivant[0] = false;
					playPane = new StackPane();
					scene1 = new Scene(playPane, Screen.getPrimary().getVisualBounds().getWidth(),
							Screen.getPrimary().getVisualBounds().getHeight() - 30);
					stage = new Stage();
				});
			} else {
				suivant[0] = true;
				num.set((num.intValue() - 1) % (liste.size()));
			}
		});

		num.set(numero);
		return true;
	}

}
