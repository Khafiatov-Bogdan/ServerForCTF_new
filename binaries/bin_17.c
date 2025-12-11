// cat > cookie.c << 'EOF'
#include <stdio.h>
#include <string.h>

int main() {
    unsigned long canary = 0x00a1b2c3;
    char buffer[64];
    
    printf("Введите форматную строку: ");
    fgets(buffer, sizeof(buffer), stdin);
    
    printf(buffer); // Утечка canary
    
    printf("\nВведите данные для переполнения: ");
    gets(buffer); // Переполнение
    
    return 0;
}
// EOF
// gcc cookie.c -fno-stack-protector -no-pie -o cookie
