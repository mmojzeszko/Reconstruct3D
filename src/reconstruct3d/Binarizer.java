//package reconstruct3d;
//
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import javax.imageio.ImageIO;
//import javax.swing.text.StyleConstants;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//
//public class Binarizer {
//    
//    BufferedImage original, grayscale, binarized;
//    BufferedImage []parts;
//    int []histogram;
//    Mat img, dest;
//    Imgproc process;
//    int i = 0;
//    
//    //Mat1b img2;
//    
//    Mat img2Mat(BufferedImage in){
//        Mat out = new Mat();
//        byte[] data;
//
//
//        out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
//        data = new byte[in.getWidth() * in.getHeight() * (int)out.elemSize()];
//        
//        int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
//        
//        for(int i = 0; i < dataBuff.length; i++)
//        {
//            data[i*3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
//            data[i*3 + 1] = (byte)((dataBuff[i] >> 8) & 0xFF);
//            data[i*3 + 2] = (byte)((dataBuff[i] >> 0) & 0xFF);
//        }
//
//
//           out.put(0, 0, data);
//           return out;
//     }
//    
//    BufferedImage mat2Img(Mat in){
//        
//        BufferedImage out;
//        byte[] data = new byte[original.getWidth() * original.getHeight() * (int)in.elemSize()];
//        int type;
//        in.get(0, 0, data);
//
//        if(in.channels() == 1)
//            type = BufferedImage.TYPE_BYTE_GRAY;
//        else
//            type = BufferedImage.TYPE_3BYTE_BGR;
//
//        out = new BufferedImage(original.getWidth(), original.getHeight(), type);
//
//        out.getRaster().setDataElements(0, 0, original.getWidth(), original.getHeight(), data);
//        
//        return out;
//    } 
//    
//    void makeHistogram(BufferedImage image){
//        
//        histogram = new int[255];
//        for(int i = 0; i < histogram.length; i++)   //zerowanie histogramu
//            histogram[i] = 0;
//        
//        for(int i=0; i<original.getWidth(); i++){
//            for(int j=0; j<original.getHeight(); j++){
//                int red = new Color(original.getRGB(i, j)).getRed();
//                int blue = new Color(original.getRGB(i, j)).getGreen();
//                int green = new Color(original.getRGB(i, j)).getBlue();
//                
//                int value = (red + green + blue)/3;
//                
//                histogram[value]++;
//                
//                
//            }
//        }
//    }
//    
//    BufferedImage binarize(BufferedImage image){
//        original = image;
//        //for(int i = 0; i < all.length; i++)
//            //return removeNoise(makeBinary(image));
//        
//        //img = img2Mat(image);
//        
//        //img = new Mat(, CvType.CV_8UC1);
//        img = Imgcodecs.imread("C:/Users/Mateusz/Documents/NetBeansProjects/Reconstruct3D/001.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
//        System.out.println("Dziala");
//        //process = new Imgproc();
//        
//        dest = new Mat();
//        
//        Size s = new Size(3, 3);
//        
//        Imgproc.GaussianBlur(img, img, s, 10f);
//        
//        Imgproc.adaptiveThreshold(img, dest, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
//        Imgcodecs.imwrite("C:/Users/Mateusz/Documents/NetBeansProjects/Reconstruct3D/001b.png", dest);
//        
//        
//        i++;
//        
//        
//        //return binarized = mat2Img(dest);
//        return image;
//           
//    }
//    
//    BufferedImage makeBinary(BufferedImage original){
//        int num = 1;
//        
//        parts = new BufferedImage[num];
//        grayscale = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//        
//        Graphics g = grayscale.getGraphics();  
//        g.drawImage(original, 0, 0, null);  
//        g.dispose();   
//        
//        int spacing = original.getWidth()/num;
//        
//        for(int i = 0; i < num; i++){
//            parts[i] = grayscale.getSubimage(0 + i*spacing, 0, spacing, original.getHeight());
//            parts[i] = myBin(parts[i]);
//        }
//        
//        binarized = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
//        
//        Graphics2D g2 = binarized.createGraphics();
//        for(int i = 0; i < num; i++)
//            g2.drawImage(parts[i], null, 0 + i*spacing, 0);
//
//        g2.dispose();
//        
//        return binarized;
//    }
//    
//    BufferedImage removeNoise(BufferedImage img){
//        BufferedImage processed = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
//        int count = 0;
//        int iterations = 3;
//        for(int k = 0; k < iterations; k++){
//            for(int i=1; i<img.getWidth()-1; i++){
//                for(int j=1; j<img.getHeight()-1; j++){
//                    count = 0;
//                    if(new Color(img.getRGB(i, j)).equals(Color.black)){
//                        //count = 0;
//
//                        if(new Color(img.getRGB(i-1, j)).equals(Color.black))
//                            count++;
//
//                        if(new Color(img.getRGB(i+1, j)).equals(Color.black))
//                            count++;
//
//                        if(new Color(img.getRGB(i, j-1)).equals(Color.black))
//                            count++;
//
//                        if(new Color(img.getRGB(i, j+1)).equals(Color.black))
//                            count++;
//                    }
//
//                    if(count < 2) 
//                        processed.setRGB(i, j, colorToRGB(new Color(img.getRGB(i, j)).getAlpha(), 255, 255, 255));
//                    else
//                        processed.setRGB(i, j, colorToRGB(new Color(img.getRGB(i, j)).getAlpha(), 0, 0, 0));
//
//                }
//            }
//        }
//        
//        return processed;
//    }
//    
//    BufferedImage myBin(BufferedImage original){
//        BufferedImage binarized2 = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
//    
//        int max = 0;
//        int min = 765;
//        
//        for(int i=0; i<original.getWidth(); i++){
//            for(int j=0; j<original.getHeight(); j++){
//                //int alpha = new Color(original.getRGB(i, j)).getAlpha();
//                int red = new Color(original.getRGB(i, j)).getRed();
//                int blue = new Color(original.getRGB(i, j)).getGreen();
//                int green = new Color(original.getRGB(i, j)).getBlue();
//                
//                int value = (red + green + blue)/3;
//                
//                if(value > max)
//                    max = value;
//                
//                if(value < min)
//                    min = value;
//                
//            
//            }
//        }
//        
//        //pixel[x,y]=255*(pixel[x,y]-minPix)/(maxPix-minPix)
////        for(int i=0; i<original.getWidth(); i++){
////            for(int j=0; j<original.getHeight(); j++){
////                
////                int alpha = new Color(original.getRGB(i, j)).getAlpha();
////                int red = new Color(original.getRGB(i, j)).getRed();
////                int blue = new Color(original.getRGB(i, j)).getGreen();
////                int green = new Color(original.getRGB(i, j)).getBlue();
////                
////                int value = (red + green + blue)/3;
////                value = 255*(value-min)/(max-min);
////                
////                int newPixel = colorToRGB(alpha, value, value, value);
////                original.setRGB(i, j, newPixel);
////                
////            }
////        }
//        
//        
//        int MONO_THRESHOLD = ((max + min)/2);
//        //System.out.println(MONO_THRESHOLD);
//        
//        for(int i=0; i<original.getWidth(); i++) {
//            for(int j=0; j<original.getHeight(); j++) {
//                
//                int alpha = new Color(original.getRGB(i, j)).getAlpha();
//                int red = new Color(original.getRGB(i, j)).getRed();
//                int blue = new Color(original.getRGB(i, j)).getGreen();
//                int green = new Color(original.getRGB(i, j)).getBlue();
//                
//                if ((red + blue + green)/3 < MONO_THRESHOLD){
//                    int newPixel = colorToRGB(alpha, 255, 255, 255);
//                    binarized2.setRGB(i, j, newPixel);
//                }
//                else{
//                    int newPixel = colorToRGB(alpha, 0, 0, 0);
//                    binarized2.setRGB(i, j, newPixel);
//                }
//                
//            }
//        }
//        
//        
//        
//        return binarized2;
//    }
//    
//    int colorToRGB(int alpha, int red, int green, int blue) {
// 
//        int newPixel = 0;
//        newPixel += alpha;
//        newPixel = newPixel << 8;
//        newPixel += red; newPixel = newPixel << 8;
//        newPixel += green; newPixel = newPixel << 8;
//        newPixel += blue;
// 
//        return newPixel;
// 
//    }
//    
//}
