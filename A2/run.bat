@echo off
java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 -cp .;"U:\Desktop\javagaming\jogl\jogamp-fat.jar";"U:\Desktop\javagaming\joml\joml-1.10.7.jar" a2.Code
