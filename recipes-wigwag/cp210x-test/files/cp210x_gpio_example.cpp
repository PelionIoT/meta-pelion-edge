#include <iostream>
#include <fcntl.h>
#include <unistd.h> 
#include <stropts.h>
#include <cstring>
#include <termios.h>

using namespace std;

int fd;
int result = 0;
unsigned long gpio =0;

#define TTY_DEVICE		"/dev/ttyUSB0"

#define IOCTL_GPIOGET		0x8000
#define IOCTL_GPIOSET		0x8001

#define G0 	0x0000
#define G1 	0x0001
#define G2 	0x0002
#define G3 	0x0004
#define G4 	0x0008
#define G5 	0x0010
#define G6 	0x0020
#define G7 	0x0040
#define G8 	0x0080
#define GP 	0x0100
#define G10 	0x0200
#define G11 	0x0400
#define G12 	0x0800
#define G13 	0x1000
#define G14 	0x2000
#define G15 	0x4000

#define G0high 	0x00010001
#define G1high 	0x00020002
#define G2high 	0x00040004
#define G3high 	0x00080008
#define G4high 	0x00100010
#define G5high 	0x00200020
#define G6high 	0x00400040
#define G7high 	0x00800080
#define G8high 	0x01000100
#define G9high		0x02000200
#define G10high	0x04000400
#define G11high	0x08000800
#define G12high	0x10001000
#define G13high	0x20002000
#define G14high	0x40004000
#define G15high	0x80008000

#define G0low	 	0x00000001
#define G1low	 	0x00000002
#define G2low	 	0x00000004
#define G3low	 	0x00000008
#define G4low	 	0x00000010
#define G5low	 	0x00000020
#define G6low	 	0x00000040
#define G7low	 	0x00000080
#define G8low	 	0x00000100
#define G9low		0x00000200
#define G10low		0x00000400
#define G11low		0x00000800
#define G12low		0x00001000
#define G13low		0x00002000
#define G14low		0x00004000
#define G15low		0x00008000






// State 1  
// 0000fffe: Original GPIO
// ffff0001: operation ~
// 00010000: operation <<
// 0001ffff: operation |=
// GPIO to set = 0001ffff, result = 0
// 00010001: New GPIO 

// State 2
// 00000001: Original GPIO 
// fffffffe: operation ~
// fffe0000: operation <<
// fffeffff: operation |=
// GPIO to set = fffeffff, result = 0
// New GPIO = fffefffe, result = 0
// goto State 1


//	Bit 0 = GPIO0 Mask
	//	Bit 1 = GPIO1 Mask
	//	Bit 2 = GPIO2 Mask
	//	Bit 3 = GPIO3 Mask
	//	Bit 4 = GPIO4 Mask
	//	Bit 5 = GPIO5 Mask
	//	Bit 6 = GPIO6 Mask
	//	Bit 7 = GPIO7 Mask
	//	Bit 8 = GPIO8 Mask
	//	Bit 9 = GPIO9 Mask
	//	Bit 10 = GPIO10 Mask
	//	Bit 11 = GPIO11 Mask
	//	Bit 12 = GPIO12 Mask
	//	Bit 13 = GPIO13 Mask
	//	Bit 14 = GPIO14 Mask
	//	Bit 15 = GPIO15 Mask
	//	Bit 16 = GPIO0 Value
	//	Bit 17 = GPIO1 Value
	//	Bit 18 = GPIO2 Value
	//	Bit 19 = GPIO3 Value
	//	Bit 20 = GPIO4 Value
	//	Bit 21 = GPIO5 Value
	//	Bit 22 = GPIO6 Value
	//	Bit 23 = GPIO7 Value
	//	Bit 24 = GPIO8 Value
	//	Bit 25 = GPIO9 Value
	//	Bit 26 = GPIO10 Value
	//	Bit 27 = GPIO11 Value
	//	Bit 28 = GPIO12 Value
	//	Bit 29 = GPIO13 Value
	//	Bit 30 = GPIO14 Value
	//	Bit 31 = GPIO15 Value
	//
	// Note: Each device supports a different number of GPIO -
	// check the data sheet for exact GPIO values available.
	// 
	// GPIO on CP2105 is exclusive to the COM port interface,
	// while on CP2108 all GPIO is accessible to any interface
void setGPIO(long pinandval, long *gpio) {

} 


int main(int argc, char *argv[])
{
	if ( argc < 2 ){ // argc should be 2 for correct execution
    		// We print argv[0] assuming it is the program name
		cout<<"usage: "<< argv[0] <<" <hex|toggle>\n";
	}
	else {
		cout << "CP210x Serial Test\n";
		fd = open(TTY_DEVICE, O_RDWR | O_NOCTTY | O_NDELAY);
		if (fd == -1)
		{
			cout << "Error opening port /dev/ttyUSB0\n";
			return -1;
		}	
		//cout << "gotsome " << argv[1] << endl;
		result = ioctl(fd, IOCTL_GPIOGET, &gpio);
		cout << "Original GPIO = " << hex << gpio << ", result = " << result << endl;

		if (strcmp(argv[1],"toggle") == 0){
			gpio = ~gpio;
			cout  << hex << gpio << ": operation ~ " << endl;
			gpio = gpio << 16;
			cout << hex <<gpio << ": operation << " << endl;
			gpio |= 0xFFFF;
			cout << hex << gpio << ": operation |= " << endl;
			//gpio = 0xFFFF;
			result = ioctl(fd, IOCTL_GPIOSET, &gpio);
			cout << "GPIO to set = " << hex << gpio << ", result = " << result << endl;

	// Now check the new GPIO value
			result = ioctl(fd, IOCTL_GPIOGET, &gpio);
			cout << "New GPIO = " << hex << gpio << ", result = " << result << endl;

		}
		else if (strcmp(argv[1],"read") == 0){
			cout << "reading..." << hex << gpio << endl;
		}
		else if (strcmp(argv[1],"set") == 0){
			if (strcmp(argv[2], "g0low") == 0){
				gpio = G0low;
			}
			else if (strcmp(argv[2], "g1low") == 0){
				gpio = G1low;
			}
			else if (strcmp(argv[2], "g2low") == 0){
				gpio = G2low;
			}
			else if (strcmp(argv[2], "g3low") == 0){
				gpio = G3low;
			}
			else if (strcmp(argv[2], "g4low") == 0){
				gpio = G4low;
			}
			else if (strcmp(argv[2], "g5low") == 0){
				gpio = G5low;
			}
			else if (strcmp(argv[2], "g6low") == 0){
				gpio = G6low;
			}
			else if (strcmp(argv[2], "g7low") == 0){
				gpio = G7low;
			}
			else if (strcmp(argv[2], "g8low") == 0){
				gpio = G8low;
			}
			else if (strcmp(argv[2], "g9low") == 0){
				gpio = G9low;
			}
			else if (strcmp(argv[2], "g10low") == 0){
				gpio = G10low;
			}
			else if (strcmp(argv[2], "g11low") == 0){
				gpio = G11low;
			}
			else if (strcmp(argv[2], "g12low") == 0){
				gpio = G12low;
			}
			else if (strcmp(argv[2], "g13low") == 0){
				gpio = G13low;
			}
			else if (strcmp(argv[2], "g14low") == 0){
				gpio = G14low;
			}
			else if (strcmp(argv[2], "g15low") == 0){
				gpio = G15low;
			}
			else if (strcmp(argv[2], "g0high") == 0){
				gpio = G0high;
			}
			else if (strcmp(argv[2], "g1high") == 0){
				gpio = G1high;
			}
			else if (strcmp(argv[2], "g2high") == 0){
				gpio = G2high;
			}
			else if (strcmp(argv[2], "g3high") == 0){
				gpio = G3high;
			}
			else if (strcmp(argv[2], "g4high") == 0){
				gpio = G4high;
			}
			else if (strcmp(argv[2], "g5high") == 0){
				gpio = G5high;
			}
			else if (strcmp(argv[2], "g6high") == 0){
				gpio = G6high;
			}
			else if (strcmp(argv[2], "g7high") == 0){
				gpio = G7high;
			}
			else if (strcmp(argv[2], "g8high") == 0){
				gpio = G8high;
			}
			else if (strcmp(argv[2], "g9high") == 0){
				gpio = G9high;
			}
			else if (strcmp(argv[2], "g10high") == 0){
				gpio = G10high;
			}
			else if (strcmp(argv[2], "g11high") == 0){
				gpio = G11high;
			}
			else if (strcmp(argv[2], "g12high") == 0){
				gpio = G12high;
			}
			else if (strcmp(argv[2], "g13high") == 0){
				gpio = G13high;
			}
			else if (strcmp(argv[2], "g14high") == 0){
				gpio = G14high;
			}
			else if (strcmp(argv[2], "g15high") == 0){
				gpio = G15high;
			}
			cout << hex << gpio <<endl;
			ioctl(fd, IOCTL_GPIOSET, &gpio);
		}
		else if (strcmp(argv[1],"a") == 0){
			gpio=0x0000FFFF;
			gpio=0x00000001;
			cout << "a: ("<<hex << gpio<<")"<<endl;
			ioctl(fd, IOCTL_GPIOSET, &gpio);
		}
		else if (strcmp(argv[1],"b") == 0){
			gpio=0xFFFFFFFF;
			gpio=0x00010001;
			cout << "b: ("<<hex << gpio<<")"<<endl;
			ioctl(fd, IOCTL_GPIOSET, &gpio);
		}
		else if (strcmp(argv[1],"c") == 0){
			gpio=0x0000FFFF;
			gpio=0x00000002;
			cout << "c: ("<<hex << gpio<<")"<<endl;
			ioctl(fd, IOCTL_GPIOSET, &gpio);
		}
		else if (strcmp(argv[1],"d") == 0){
			gpio=0xFFFFFFFF;
			gpio=0x00020002;
			cout << "d: ("<<hex << gpio<<")"<<endl;
			ioctl(fd, IOCTL_GPIOSET, &gpio);
		}
		else {
			cout << "would normally do what you want.  Someone write me\n";
		}
		close(fd);
		return 0;		
	}
}




	// cout << "original gpio = ";
	// cout << hex << gpio << endl;
	// gpio = ~gpio;
	// gpio = gpio << 8;
	// gpio |= 0x00FF;
	// cout << "gpio = ";
	// cout << hex << gpio << endl;
	// ioctl(fd, 0x8001, &gpio);
	// ioctl(fd, 0x8000, &gpio);
	// cout << "new gpio = ";
	// cout << hex << gpio << endl;


