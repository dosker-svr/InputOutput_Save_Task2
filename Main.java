package InputOutput_Save_Task2;

import java.io.*;
import java.io.FileFilter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) {
        GameProgress save1 = new GameProgress(90, 5, 2, 45.2);
        GameProgress save2 = new GameProgress(70, 7, 3, 78.3);
        GameProgress save3 = new GameProgress(30, 2, 4, 101.6);

        String pathWithSavedGames = "C:\\NetologyProjects\\JavaCoreNetology\\src\\InputOutput_Installation_Task1\\Games\\savegames";

        saveGame(pathWithSavedGames, "save_1.dat", save1);
        saveGame(pathWithSavedGames, "save_2.dat", save2);
        saveGame(pathWithSavedGames, "save_3.dat", save3);

        zipFiles(pathWithSavedGames, "zip_1.zip");

        deleteDatFiles("C:\\NetologyProjects\\JavaCoreNetology\\src\\InputOutput_Installation_Task1\\Games\\savegames");

//        openZip("zip_1.zip", pathWithSavedGames);

    }

    public static void saveGame(String pathWithSavedGames, String fileNameToSavingGame, GameProgress gameProgress) {
//        String path = "C:\\NetologyProjects\\JavaCoreNetology\\src\\InputOutput_Installation_Task1\\Games\\savegames";
        File saveGame = new File(pathWithSavedGames, fileNameToSavingGame);
        try {
            if (saveGame.createNewFile()) System.out.println("Игра сохранена в файле - " + saveGame.getName());
        } catch (IOException e) {
            System.out.println(e.getMessage() + " находимся в catch создания файла сохранения");
        }

//        byte[] bytesGameProgress = gameProgress.toString().getBytes();
        FileOutputStream saveGameStream = null;
        ObjectOutputStream serializationSavingGame = null;
        try {
            saveGameStream = new FileOutputStream(saveGame, false);
            serializationSavingGame = new ObjectOutputStream(saveGameStream);
            serializationSavingGame.writeObject(gameProgress);
        } catch (IOException e) {
            System.out.println(e.getMessage() + " - находимся в catch записи в файл сохранения + поток сериализации");
        } finally {
            try {
                if (serializationSavingGame != null) {
                    serializationSavingGame.close();
//                    System.out.println("Закрыли поток сериализации");
                }
                if (saveGameStream != null) {
                    saveGameStream.close();
//                    System.out.println("Закрыли поток записи в файл");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage() + " - находимся в catch закрытия стримов");
            }
        }
    }


    public static void zipFiles(String namePathWithZipFiles, String nameZip) {
        File pathWithZipFile = new File(namePathWithZipFiles);
        File zipFile = new File(namePathWithZipFiles, nameZip);

        try (FileOutputStream fileZipStream = new FileOutputStream(zipFile, true);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileZipStream)) {

//            File[] files = pathWithZipFile.listFiles(pathname -> pathname.getName().endsWith(".dat"));
            File[] files = pathWithZipFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".dat");
                }
            });

            for (File file : files) {
                FileInputStream savedFile = new FileInputStream(file);
                byte[] bytesDirectory = new byte[savedFile.available()];
                savedFile.read(bytesDirectory);

                savedFile.close();
//                System.out.println("Закрыли поток чтения директории");

                ZipEntry entry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(entry);
                zipOutputStream.write(bytesDirectory);
            }

            zipOutputStream.closeEntry();

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage() + " ошибос FileNotFoundException");
        } catch (IOException e) {
            System.out.println(e.getMessage() + " - находимся в catch закрытия стримов ZIP");
        }
    }


    public static void deleteDatFiles(String pathWithSavedGames) {
        File folderWithSavedGames = new File(pathWithSavedGames);
//      File[] files = folderWithSavedGames.listFiles(pathname -> pathname.getName().endsWith(".dat"));
        File[] files = folderWithSavedGames.listFiles(new FileFilter() {
                                                            @Override
                                                            public boolean accept(File pathname) {
                                                                return pathname.getName().endsWith(".dat");
                                                            }
        });

//        for (int i = 0; i < files.length; i++) {
//            if (files[i].delete()) {
//                System.out.println("Удалили файл - " + files[i].getName() + "\n");
//            }
//        }

        for (File file : files) {
            if (file.delete()) {
                System.out.println("Удалили файл - " + file.getName() + "\n");
            }
        }
    }

    public static void openZip(String nameZip, String pathWithSavedGames) {

//        File zip = new File(pathWithSavedGames, nameZip);
        File zip = new File("src\\InputOutput_Installation_Task1\\Games\\savegames\\zip_1.zip");
        File path = new File(pathWithSavedGames);

        FileOutputStream writeSave = null;

        try (FileInputStream fileInputStream = new FileInputStream(zip);
             ZipInputStream zipInputStream = new ZipInputStream(fileInputStream)) {


            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File fileInZip = new File(path, ("\\" + entry.getName()));
                writeSave = new FileOutputStream(fileInZip);

                byte[] buffer = new byte[4096];
                int count;
                while ((count = zipInputStream.read(buffer)) > -1) {
                    writeSave.write(buffer, 0, count);
                }
//                zipInputStream.closeEntry();
            }


        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (writeSave != null) writeSave.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}







/*Вопрос: в методе saveGame я не использовал РЕСУРСЫ. при закрытие потоков (потока записи в файл и потока сериализации): - имеет ли значение какой поток закрывать первым.
*
* Коряво получился метод zipFiles. Подскажите как можно его сделать по-другому.
*
* Можно ли в методе deleteDatFiles в цикле for each удалять элементы массива?
*
* Делал задачу 3. Не понимаю почему в методе openZip во 2ом цикле while не получается считать байты в массив buffer.
* И вообще не понял как можно нормально распаковать архив. Объясните пожалуйста как это происходит.
* Моё понимание такое:
* 1-Создаю File для zip_1.zip
* 2-Открываю поток чтения архива(ZIS) из потока чтения(FIS) из zip_1.zip
* 3-Беру объект архива, считываю его байты в массив байтов.
* И записываю их в созданный файл в директории (вот в этом пункте не понимаю как считать байты у объекта архива) */