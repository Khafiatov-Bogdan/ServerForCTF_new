// cat > partial.c << 'EOF'
#include <stdio.h>
#include <string.h>

void win() {
    printf("CTF{partial_win_achieved}\n");
}

void vuln() {
    char buffer[64];
    printf("Введите данные: ");
    gets(buffer);
}

int main() {
    vuln();
    return 0;
}
// EOF
// gcc partial.c -fno-stack-protector -no-pie -o partial
