// cat > uaf.c << 'EOF'
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct chunk {
    char data[32];
};

int main() {
    struct chunk *c = malloc(sizeof(struct chunk));
    strcpy(c->data, "CTF{use_after_free_done}");
    
    free(c); // Освобождаем память
    
    // Use After Free
    printf("Данные (после free): %s\n", c->data);
    
    return 0;
}
// EOF
// gcc uaf.c -o uaf -no-pie
