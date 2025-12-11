// cat > ret2libc.c << 'EOF'
#include <stdio.h>
#include <string.h>

int main() {
    char buffer[64];
    
    printf("Введите данные: ");
    gets(buffer);
    
    puts("Программа завершена");
    return 0;
}
// EOF
// gcc ret2libc.c -fno-stack-protector -no-pie -o ret2libc
