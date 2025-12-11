// cat > ro.c << 'EOF'
#include <stdio.h>

const char* flag = "CTF{rodata_reveals_all}";

int main() {
    printf("Программа запущена.\n");
    return 0;
}
// EOF
// gcc ro.c -o ro.exe -no-pie
