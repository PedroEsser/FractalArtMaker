package fractalKernels;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;
import java.util.HashSet;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;

public class Mandelbrot64 extends JFrame{
    private final int dx = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int dy = Toolkit.getDefaultToolkit().getScreenSize().height;
    private final double scale = 3.0;
    private final BufferedImage image = new BufferedImage(dx, dy, BufferedImage.TYPE_INT_RGB);
    private final int[] rgb = ((DataBufferInt)image.getRaster().getDataBuffer()).getData(); // extract the underlying RGB buffer from the image
    private final int group = 4; // size of GPU shader groups (default: 32)
    private final Range range = Range.create2D(selectGPU(), dx-dx%group+(dx%group==0?0:group), dy-dy%group+(dy%group==0?0:group), group, group);
    private final MandelKernel kernel = new MandelKernel(dx, dy, rgb);
    private double mx=dx/2, my=dy/2;
    private double s=scale, x=-0.5, y=0.0;
    
    public static void main(String[] args) {
        new Mandelbrot64();
    }
    public Mandelbrot64() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(Color.BLACK);
        setVisible(true);
        
        update(scale, x, y);
        
        HashSet<Integer> pressed = new HashSet<Integer>();
        MouseAdapter mouseAd = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mx = (double)(e.getX()-dx/2)/dy;
                my = (double)(e.getY()-dy/2)/dy;
                if(e.getButton() == MouseEvent.BUTTON1) pressed.add(-1);
                if(e.getButton() == MouseEvent.BUTTON3) pressed.add(-2);
                if(e.getButton() == MouseEvent.BUTTON2) {
                    mx += x/s;
                    my += y/s;
                    pressed.add(-3);
                }
            }
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) pressed.remove(-1);
                if(e.getButton() == MouseEvent.BUTTON3) pressed.remove(-2);
                if(e.getButton() == MouseEvent.BUTTON2) pressed.remove(-3);
            }
            public void mouseDragged(MouseEvent e) {
                if(pressed.contains(-3)) {
                    double px = (double)(e.getX()-dx/2)/dy;
                    double py = (double)(e.getY()-dy/2)/dy;
                    x = (mx-px)*s;
                    y = (my-py)*s;
                    update(s, x, y);
                }
            }
        };
        addMouseListener(mouseAd);
        addMouseMotionListener(mouseAd);
        Thread keyAction = new Thread("keyAction") {
            public void run() {
                double st;
                double zoom=0.0, l=scale;
                
                while(true) {
                    st = time();
                    
                    if(pressed.contains(-1) && !pressed.contains(-2)) {
                        zoom--;
                        s = scale*Math.exp(zoom*0.1);
                        x -= mx*(s-l);
                        y -= my*(s-l);
                        update(s, x, y);
                        l = s;
                    }
                    if(pressed.contains(-2) && !pressed.contains(-1)) {
                        zoom++;
                        s = scale*Math.exp(zoom*0.1);
                        x -= mx*(s-l);
                        y -= my*(s-l);
                        update(s, x, y);
                        l = s;
                    }
                    
                    pause(1.0/60.0-time()+st);
                }
            }
        };
        keyAction.start();
    }
    public void update(double scale, double x, double y) {
        kernel.setArea(scale, x, y);
        long t = System.currentTimeMillis();
        kernel.execute(range);
        System.out.println(System.currentTimeMillis() - t + " millis passed, " + kernel.getTargetDevice());
        getGraphics().drawImage(image, 0, 0, null);
    }
    
    public class MandelKernel extends Kernel{
        @Constant private final int dx, dy;
        private int rgb[];
        @Constant private final int MAX = 1024*4; // maximum iterations
        @Constant final private int texture[] = new int[MAX]; // texture map
        private double scale = 0f, ox=0f, oy=0f;
        
        public MandelKernel(int dx, int dy, int[] rgb) {
            this.dx = dx;
            this.dy = dy;
            this.rgb = rgb;
            for(int i=0; i<MAX/2; i++) {
                texture[i] = Color.HSBtoRGB(160f/360f, 1f, 2f*(float)i/MAX);
            }
            for(int i=MAX/2; i<MAX; i++) {
                texture[i] = Color.HSBtoRGB(160f/360f, 1f, 2f*(1f-(float)i/MAX));
            }
        }
        public void setArea(double scale, double ox, double oy) {
            this.scale = scale;
            this.ox = ox;
            this.oy = oy;
        }
        
        @Override public void run() {
            final double x = (getGlobalId(0)*scale-scale*0.5*dx)/dy+ox;
            final double y = (getGlobalId(1)*scale-scale*0.5*dy)/dy+oy;
            int c = 0;
            double zx=x, zy=y, temp=0f;
            while(c < MAX && zx*zx+zy*zy < 8.0) {
                temp = zx*zx-zy*zy + x;
                zy = 2.0*zx*zy + y;
                zx = temp;
                c++;
            }
            rgb[getGlobalId(1)*dx+getGlobalId(0)] = texture[c]; // get texture for iteration number
        }
    }
    private static Device selectGPU() {
        return OpenCLDevice.select(new OpenCLDevice.DeviceComparitor() {
            public OpenCLDevice select(OpenCLDevice integrated, OpenCLDevice dedicated) {
                if(dedicated.getType() == Device.TYPE.GPU) {
                    return dedicated;
                } else if(integrated.getType() == Device.TYPE.GPU) {
                    return integrated;
                } else {
                    return null;
                }
            }
        });
    }
    
    private static double time() {
        return System.nanoTime()*1E-9;
    }
    private static void pause(double t) {
        if(t > 0) {
            try {
                Thread.sleep((int)(t*1E3));
            } catch(Exception e) {}
        }
    }
}