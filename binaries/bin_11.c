// cat > ret2win.c << 'EOF'
#include <stdio.h>
#include <string.h>

void win() {
    printf("CTF{ret2win_master}\n");
}

void vulnerable() {
    char buffer[64];
    printf("Введите данные: ");
    gets(buffer);
}

int main() {
    vulnerable();
    return 0;
}
// EOF
// gcc ret2win.c -fno-stack-protector -no-pie -o ret2win
