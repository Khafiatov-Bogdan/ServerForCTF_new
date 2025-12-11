// cat > double_free.c << 'EOF'
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main() {
    char *p1 = malloc(32);
    strcpy(p1, "CTF{double_free_won}");
    
    free(p1);
    free(p1); // Двойное освобождение!
    
    char *p2 = malloc(32);
    char *p3 = malloc(32);
    
    printf("Данные: %s\n", p3); // Может показать старые данные
    
    return 0;
}
// EOF
// gcc double_free.c -o double_free -no-pie
