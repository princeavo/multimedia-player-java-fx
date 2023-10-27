package com;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

class Bouton extends Button {

	public Bouton() {
		super();
		this.setTextFill(Color.DARKBLUE);
		this.focusedProperty().addListener((v,o,n)->{
			if(Boolean.TRUE.equals(n)) {
				this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), null)));
			}else {
				this.setBorder(null);
			}
		});
	}

	public Bouton(String text) {
		this();
		this.setText(text);
	}

	public Bouton(String text, Node graphic) {
		this(text);
		this.setGraphic(graphic);
	}

}
