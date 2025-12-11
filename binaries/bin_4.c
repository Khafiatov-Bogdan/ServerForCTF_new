// cat > vuln.c << 'EOF'
#include <stdio.h>
#include <string.h>

void secret() {
    printf("CTF{overflow_detected}\n");
}

int main() {
    char buffer[32];
    
    printf("Введите данные: ");
    gets(buffer); // Опасная функция!
    
    printf("Вы ввели: %s\n", buffer);
    return 0;
}
// EOF
// gcc vuln.c -fno-stack-protector -z execstack -o vuln -no-pie
