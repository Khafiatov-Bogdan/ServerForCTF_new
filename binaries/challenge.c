#include <stdio.h>
#include <string.h>
#include <stdlib.h>

const char* flag = "CTF{str1ngs_aRe_p0werful}";

void never_called() {
    printf("Flag is: %s\n", flag);
}

const char* flags[] = {
    "CTF{this_is_not_the_real_flag}",
    "FLAG{try_harder}",
    "CTF{debug_me_if_you_can}",
    "NOT_A_FLAG{just_a_distraction}"
};

int main() {
    char input[100];
    printf("В этой программе спрятан флаг.\n");
    
    printf("Попробуйте угадать флаг: ");
    fgets(input, sizeof(input), stdin);

    input[strcspn(input, "\n")] = 0;

    int found = 0;
    for (int i = 0; i < sizeof(flags)/sizeof(flags[0]); i++) {
        if (strcmp(input, flags[i]) == 0) {
            printf("Хм... почти, но это не настоящий флаг!\n");
            found = 1;
            break;
        }
    }
    
    if (!found) {
        if (strstr(input, "CTF{") != NULL) {
            printf("Интересная попытка, но это не тот флаг.\n");
        } else {
            printf("Это не похоже на флаг формата CTF{...}\n");
        }
    }
    
    return 0;
}