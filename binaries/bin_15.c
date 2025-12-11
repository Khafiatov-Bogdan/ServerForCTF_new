// cat > got_leak.c << 'EOF'
#include <stdio.h>

int main() {
    char input[100];
    
    printf("Введите форматную строку: ");
    fgets(input, sizeof(input), stdin);
    
    printf(input); // Утечка адресов GOT
    
    puts("\nПрограмма завершена");
    return 0;
}
// EOF
// gcc got_leak.c -o got_leak -no-pie
