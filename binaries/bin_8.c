// cat > disasm.asm << 'EOF'
// section .data
//     flag db 'CTF{disassembly_never_lies}',0
//     msg db 'Программа запущена',0

// section .text
//     global _start

// _start:
//     ; Инструкция с адресом флага
//     mov rdi, flag
//     mov rax, 60
//     mov rdi, 0
//     syscall
// EOF
// if command -v nasm &> /dev/null && command -v ld &> /dev/null; then
//     nasm -f elf64 disasm.asm -o disasm.o
//     ld disasm.o -o disasm
//     echo "Бинарник создан: binaries/disasm"
// else
//     # Альтернатива на C
//     cat > disasm.c << 'EOF'
int main() {
    char* flag = "CTF{disassembly_never_lies}";
    return 0;
}
// EOF
//     gcc disasm.c -o disasm -no-pie

