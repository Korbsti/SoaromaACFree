package me.korbsti.soaromaac.movementreplayer;

import me.korbsti.soaromaac.Main;

import java.io.*;

public class WriteJS {
    Main plugin;

    public WriteJS(Main instance) {
        this.plugin = instance;
    }

    public void writeJS(String uuid, String date) {

        if (new File(System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "replayer" + File.separator + "output").mkdir()) {
        }

        boolean doesExist = true;
        int num = 0;
        while (doesExist) {
            if (!new File(System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "replayer" + File.separator + "output" + File.separator + "index_" + num + ".html").exists()) {
                doesExist = false;
                try {
                    new File(System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "replayer" + File.separator + "output" + File.separator + "index_" + num + ".html").createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            }
            num++;
        }
        try {
            String blockX = "";
            String blockY = "";
            String blockZ = "";

            String playerX = "";
            String playerY = "";
            String playerZ = "";

            FileReader fr = new FileReader(System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "replayer" + File.separator + date + File.separator + uuid + ".txt");
            BufferedReader br = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                String[] list = currentLine.split(",");
                if (currentLine.startsWith("b")) {
                    blockX += list[1] + ", ";
                    blockY += list[2] + ", ";
                    blockZ += list[3] + ", ";
                } else {
                    playerX += list[1] + ", ";
                    playerY += list[2] + ", ";
                    playerZ += list[3] + ", ";
                }

            }
            br.close();
            fr.close();
            FileWriter writer = new FileWriter(new File(System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "replayer" + File.separator + "output" + File.separator + "index_" + num + ".html"));

            writer.write("\r\n" + "<!doctype html>\r\n" + "\r\n" + "<html>\r\n" + "   <head>\r\n" + "      <title>SAC Player replay</title>\r\n" + "   </head>\r\n" + "   <body>\r\n" + "      <script type = \"module\">\r\n" + "    import * as THREE from 'https://cdn.skypack.dev/three@v0.131.1';\r\n" + "    import { OrbitControls } from 'https://cdn.skypack.dev/three@v0.131.1/examples/jsm/controls/OrbitControls.js';\r\n" + "    const blockX = new Array(" + blockX + ");\r\n" + "    const blockY = new Array(" + blockY + ");\r\n" + "    const blockZ = new Array(" + blockZ + ");\r\n" + "    const playerX = new Array(" + playerX + ");\r\n" + "    const playerY = new Array(" + playerY + ");\r\n" + "    const playerZ = new Array(" + playerZ + ");\r\n" + "    const newCube = new Array();\r\n" + "    const scene = new THREE.Scene();\r\n" + "    var camera = new THREE.PerspectiveCamera(75, window.innerWidth/window.innerHeight, 1, 10000);\r\n" + "    var renderer = new THREE.WebGLRenderer();\r\n" + "    renderer.setSize(window.innerWidth, window.innerHeight);\r\n" + "    document.body.appendChild(renderer.domElement);\r\n" + "    const controls = new OrbitControls( camera, renderer.domElement );\r\n" + "    var cube = new THREE.BoxGeometry(1, 1, 1, 1, 1, 1);\r\n" + "    \r\n" + "    var material = new THREE.MeshBasicMaterial({color: 0xfffff, wireframe: true});\r\n" + "    var playerMaterial = new THREE.MeshBasicMaterial({color: 0xff0000, wireframe: true});\r\n" + "    for(var i = 0, size = blockX.length; i < size ; i++){\r\n" + "        newCube[i] = (new THREE.Mesh(cube, material));\r\n" + "    }\r\n" + "    for(var i = 0, size = blockX.length; i < size ; i++){\r\n" + "        scene.add(newCube[i]);\r\n" + "        newCube[i].position.x = blockX[i];\r\n" + "        newCube[i].position.y = blockY[i];\r\n" + "        newCube[i].position.z = blockZ[i];\r\n" + "    }\r\n" + "    var playerCube = new THREE.Mesh(cube, playerMaterial);\r\n" + "    scene.add(playerCube);\r\n" + "    playerCube.position.x = playerX[0];\r\n" + "    playerCube.position.y = playerY[0];\r\n" + "    playerCube.position.z = playerZ[0];\r\n" + "    var bb = new THREE.Box3()\r\n" + "    bb.setFromObject(playerCube);\r\n" + "    bb.center(controls.target);\r\n" + "    var xx = 0;\r\n" + "    var timer = setInterval(onPlayerUpdate, 30);\r\n" + "    function onPlayerUpdate(){\r\n" + "        if(playerX[xx] != null){\r\n" + "            playerCube.position.x = playerX[xx];\r\n" + "            playerCube.position.y = playerY[xx];\r\n" + "            playerCube.position.z = playerZ[xx];\r\n" + "            xx++;\r\n" + "            } else {\r\n" + "            xx = 0;\r\n" + "            }\r\n" + "    }\r\n" + "    function render() {\r\n" + "        requestAnimationFrame(render);\r\n" + "        renderer.render(scene, camera);\r\n" + "        controls.update();\r\n" + "       timer.setInterval(30);\r\n" + "    };\r\n" + "    render();\r\n" + "      </script>\r\n" + "   </body>\r\n" + "</html>\r\n" + "");

            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
