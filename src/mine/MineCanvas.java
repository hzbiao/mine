package mine;

import java.util.Date;
import java.util.Random;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class MineCanvas extends Canvas implements CommandListener {
	private  MineMIDlet mianMIDlet;
	private  Display display;

	private int gridSize = 8;
	private int mineCount = 10;
	private  int width, height, selx, sely, leftbomb;
	private  byte grid[][]; // 用于存放扫雷数据
	private  boolean gameover; // 是否结束
	private  Random rand; // 产生随机数

	private  Image exScreenImg; // 扩展图像层
	private  Graphics g;
	private  Image titleImg, mineImg, fmineImg, hideImg, fhideImg,
			flagImg, fflagImg, numImg[], fnumImg[];

	// 定义菜单
	private static Command startCmd = new Command("重新游戏", Command.OK, 1);
	private static Command exitCmd = new Command("退出", Command.EXIT, 2);

	private int degree;
	public MineCanvas(MineMIDlet mine,int degree) {
		super();
		this.degree = degree;
		setMineCount();
		mianMIDlet = mine;

		grid = new byte[height][width];
		rand = new Random((new Date()).getTime());
		numImg = new Image[8];
		fnumImg = new Image[8];

		gameover = true;

		int i;
		// 预先载入图片
		try {
			titleImg = Image.createImage("/images/title.png");
			mineImg = Image.createImage("/images/mine.png");
			fmineImg = Image.createImage("/images/minef.png");
			hideImg = Image.createImage("/images/hide.png");
			fhideImg = Image.createImage("/images/hidef.png");
			flagImg = Image.createImage("/images/flag.png");
			fflagImg = Image.createImage("/images/flagf.png");
			for (i = 1; i <= 8; i++) {
				numImg[i - 1] = Image.createImage("/images/n" + i + ".png");
				fnumImg[i - 1] = Image.createImage("/images/n" + i + "f.png");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// 初始化图像缓存（宽度必须为8的倍数）
		exScreenImg = Image.createImage(width * gridSize + 15, height
				* gridSize + 1);
		g = exScreenImg.getGraphics();

		// Snd = true;
		// Vib = true;

		// if (Snd) SndCmd = new Command(closeSnd, Command.OK, 4);
		// else SndCmd = new Command(openSnd, Command.OK, 4);
		// if (Vib) VibCmd = new Command(closeVib, Command.OK, 5);
		// else VibCmd = new Command(openVib, Command.OK, 5);

		// 添加菜单
		addCommand(startCmd);
		addCommand(exitCmd);
		// addCommand(HelpCmd);
		// addCommand(SndCmd);
		// addCommand(VibCmd);
		setCommandListener(this);

		// 画标题
		g.drawImage(titleImg, 0, 0, 20);
		newGame();
	}

	public void activate(Display disp) {
		display = disp;
		display.setCurrent(this); // 设置显示目标
		setCommandListener(this); // 监视菜单选择
	}

	protected void paint(Graphics g) {
		// TODO Auto-generated method stub
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.drawImage(exScreenImg, 0, 0, 0);
	}

	protected void keyPressed(int kcode) // 按键响应
	{
		if (gameover)
			return;

		int bomb = 0;
		int oldx = selx;
		int oldy = sely;
		System.out.println("keyCode :"+kcode);
		switch (kcode) {
		case '1':
		case -5:// 1,挖开
			System.gc(); // 先释放无用的资源
			if (grid[sely][selx] >= 10 && grid[sely][selx] < 20) {
				grid[sely][selx] -= 10;
				if (grid[sely][selx] == 0)
					Expand(selx, sely);
			} else if (grid[sely][selx] < 10) {
				if (!SafeExp(selx, sely))
					bomb = 1;
			}
			break;

		case '3': // 3,作标记
			System.gc();
			if (grid[sely][selx] >= 10) {
				if (grid[sely][selx] < 20) {
					grid[sely][selx] += 10;
					leftbomb--;
				} else {
					grid[sely][selx] -= 10;
					leftbomb++;
				}
			}
			break;

		case '2': // 2
		case -59: // 上键
		case -1:
			sely--;
			break;
		case '4': // 4
		case -61: // 左向键
		case -3:
			selx--;
			break;
		case '6': // 6
		case -62: // 右向键
		case -4:
			selx++;
			break;
		case '5': // 5
		case '8': // 8
		case -60: // 下键
		case -2:
			sely++;
			break;
		}
		if (selx < 0)
			selx = 0;
		if (selx > width - 1)
			selx = width - 1;
		if (sely < 0)
			sely = 0;
		if (sely > height - 1)
			sely = height - 1;

		DrawBlock(oldx, oldy, false);
		if (bomb == 0)
			bomb = DrawBlock(selx, sely, true);
		else
			DrawBlock(selx, sely, true);

		 g.setColor(0xffffff);
		g.fillRect(width * gridSize + 2, 26, 13, 13);
		g.setColor(0);

		g.drawString("" + leftbomb, width * gridSize + 15, 26, Graphics.RIGHT
				| Graphics.TOP);

		if (bomb == 1) {
			gameover = true;
			// SoundPlay(1);
			// if (Vib) Vibrator.triggerVibrator(150);
			g.drawString("爆", width * gridSize + 15, 39, Graphics.RIGHT
					| Graphics.TOP);
			g.drawString("炸", width * gridSize + 15, 52, Graphics.RIGHT
					| Graphics.TOP);
		}
		if (Judge()) {
			gameover = true;
			// SoundPlay(0);
			g.drawString("成", width * gridSize + 15, 39, Graphics.RIGHT
					| Graphics.TOP);
			g.drawString("功", width * gridSize + 15, 52, Graphics.RIGHT
					| Graphics.TOP);
		}
		this.repaint();
		// exScreenImg.blitToScreen(0,0);
	}

	protected void keyRepeated(int kcode) // 按钮连按响应
	{
		if (gameover)
			return;

		int oldx = selx;
		int oldy = sely;
		switch (kcode) {
		case '2':
		case -59: // up
		case -1:
			sely--;
			break;
		case '4':
		case -61: // LEFT
		case -3:
			selx--;
			break;
		case '6':
		case -62: // RIGHT
		case -4:
			selx++;
			break;
		case '5':
		case '8':
		case -60: // DOWN
		case -2:
			sely++;
			break;
		}
		if (selx < 0)
			selx = 0;
		if (selx > width - 1)
			selx = width - 1;
		if (sely < 0)
			sely = 0;
		if (sely > height - 1)
			sely = height - 1;

		DrawBlock(oldx, oldy, false);
		DrawBlock(selx, sely, true);
		this.repaint();
		// exScreenImg.blitToScreen(0,0);
	}

	// 是否全部完成判断
	private boolean Judge() {
		if (leftbomb == 0) {
			int i, j;
			for (i = 0; i < height; i++) {
				for (j = 0; j < width; j++) {
					if (grid[i][j] >= 10 && grid[i][j] < 20)
						return false;
				}
			}
			return true;
		} else
			return false;
	}

	// 菜单选择响应
	public void commandAction(Command cmd, Displayable displayable) {
//		boolean savepara = false;
		if (cmd == exitCmd)
			mianMIDlet.comeBack();
		else if (cmd == startCmd) {
			newGame();
		}
	}
	public void setMineCount(){
		if(degree==1){
			mineCount =10;
			width = 10;
			height = 10;
		}else if(degree ==2 ){
			mineCount = 40;
			width = 20;
			height = 20;
		}else if(degree ==3){
			mineCount = 99;
			width = 28;
			height = 28;
		}
	}
	// 开始新游戏
	public void newGame() {
		
		gameover = false;
		selx = 0;
		sely = 0;
		leftbomb = mineCount;

		int i, j, x, y;
		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++)
				grid[i][j] = 10;
		}

		for (i = 0; i < mineCount; i++) {
			while (true) {
				x = Math.abs(rand.nextInt()) % width;
				y = Math.abs(rand.nextInt()) % height;
				if (grid[y][x] != 19) {
					grid[y][x] = 19;
					break;
				}
			}
		}

		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++) {

				if (grid[i][j] == 19)
					continue;
				int k, l;
				for (k = -1; k < 2; k++) {
					if (i + k < 0)
						continue;
					if (i + k >= height)
						continue;
					for (l = -1; l < 2; l++) {
						if (l + j < 0)
							continue;
						if (l + j >= width)
							continue;
						if (k == 0 && l == 0)
							continue;

						if (grid[i + k][j + l] == 19)
							grid[i][j]++;
					}
				}
			}
		}

		g.setColor(0xffffff);
		g.fillRect(0, 0, exScreenImg.getWidth(), exScreenImg.getHeight());
		// g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(0);
		// exScreenImg.clear((byte)0);
		for (i = 0; i <= width; i++) {
			g.drawLine(i * gridSize, 0, i * gridSize, height * gridSize);
		}
		for (i = 0; i <= height; i++) {
			g.drawLine(0, i * gridSize, width * gridSize, i * gridSize);
		}

		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++) {
				g.drawImage(hideImg, j * gridSize + 1, i * gridSize + 1, 20);
			}
		}
		g.drawImage(fhideImg, selx * gridSize + 1, sely * gridSize + 1, 20);
		g.drawString("剩", width * gridSize + 15, 0, Graphics.RIGHT
				| Graphics.TOP);
		g.drawString("余", width * gridSize + 15, 13, Graphics.RIGHT
				| Graphics.TOP);
		g.drawString(""+leftbomb, width*gridSize+15, 26, Graphics.RIGHT |
		 Graphics.TOP);
		this.repaint();

		// SoundPlay(2);
	}

	// 画一个格子
	// focus标示着个格子是否为焦点,如果为true,则要画反色图形
	private int DrawBlock(int x, int y, boolean focus) {
		int retval = 0;
		if (grid[y][x] == 0) {
			if (!focus)
				g.setColor(0xffffff);
			g.fillRect(x * gridSize + 1, y * gridSize + 1, gridSize - 1,
					gridSize - 1);
			if (!focus)
				g.setColor(0);
		} else if (grid[y][x] > 0 && grid[y][x] < 9) {
			if (focus)
				g.drawImage(fnumImg[grid[y][x] - 1], x * gridSize + 1, y
						* gridSize + 1, 20);
			else
				g.drawImage(numImg[grid[y][x] - 1], x * gridSize + 1, y
						* gridSize + 1, 20);
		} else if (grid[y][x] == 9) {
			int i, j;
			for (i = 0; i < height; i++) {
				for (j = 0; j < width; j++) {
					if (grid[i][j] == 19 || grid[i][j] == 29)
						g.drawImage(mineImg, j * gridSize + 1, i * gridSize
								+ 1, 20);
				}
			}
			if (focus)
				g.drawImage(fmineImg, x * gridSize + 1, y * gridSize + 1, 20);

			retval = 1;
		} else if (grid[y][x] >= 10 && grid[y][x] < 20) {
			if (focus)
				g.drawImage(fhideImg, x * gridSize + 1, y * gridSize + 1, 20);
			else
				g.drawImage(hideImg, x * gridSize + 1, y * gridSize + 1, 20);
		} else {
			if (focus)
				g.drawImage(fflagImg, x * gridSize + 1, y * gridSize + 1, 20);
			else
				g.drawImage(flagImg, x * gridSize + 1, y * gridSize + 1, 20);
		}

		return retval; // 返回值：1-画的是地雷；0-不是
	}

	private void Expand(int x, int y) {
		int i, j;
		for (i = -1; i < 2; i++) {
			if (y + i < 0)
				continue;
			if (y + i >= height)
				continue;
			for (j = -1; j < 2; j++) {
				if (x + j < 0)
					continue;
				if (x + j >= width)
					continue;
				if (i == 0 && j == 0)
					continue;

				if (grid[y + i][x + j] >= 10 && grid[y + i][x + j] < 20) {
					grid[y + i][x + j] -= 10;
					DrawBlock(x + j, y + i, false);
					if (grid[y + i][x + j] == 0)
						Expand(x + j, y + i);
				}
			}
		}
	}

	private boolean SafeExp(int x, int y) {
		int i, j, flag = 0;
		for (i = -1; i < 2; i++) {
			if (y + i < 0)
				continue;
			if (y + i >= height)
				continue;
			for (j = -1; j < 2; j++) {
				if (x + j < 0)
					continue;
				if (x + j >= width)
					continue;
				if (i == 0 && j == 0)
					continue;
				if (grid[y + i][x + j] > 20)
					flag++;
			}
		}
		if (flag != grid[y][x])
			return true;

		boolean retval = true;
		for (i = -1; i < 2; i++) {
			if (y + i < 0)
				continue;
			if (y + i >= height)
				continue;
			for (j = -1; j < 2; j++) {
				if (x + j < 0)
					continue;
				if (x + j >= width)
					continue;
				if (i == 0 && j == 0)
					continue;
				if (grid[y + i][x + j] == 19) // 翻到地雷
				{
					grid[y + i][x + j] = 9;
					DrawBlock(x + j, y + i, true);
					retval = false;
				} else if (grid[y + i][x + j] > 20 && grid[y + i][x + j] != 29) // 在标记错误的地方画叉
				{
					g.drawLine((x + j) * gridSize + 1,
							(y + i) * gridSize + 1, (x + j + 1) * gridSize - 1,
							(y + i + 1) * gridSize - 1);
					g.drawLine((x + j) * gridSize + 1, (y + i + 1) * gridSize
							- 1, (x + j + 1) * gridSize - 1, (y + i) * gridSize
							+ 1);
				}
			}
		}

		if (retval) {
			for (i = -1; i < 2; i++) {
				if (y + i < 0)
					continue;
				if (y + i >= height)
					continue;
				for (j = -1; j < 2; j++) {
					if (x + j < 0)
						continue;
					if (x + j >= width)
						continue;
					if (i == 0 && j == 0)
						continue;

					if (grid[y + i][x + j] >= 10 && grid[y + i][x + j] < 20) {
						grid[y + i][x + j] -= 10;
						DrawBlock(x + j, y + i, false);
						if (grid[y + i][x + j] == 0)
							Expand(x + j, y + i);
					}
				}
			}
		}
		return retval;
	}

}
