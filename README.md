# Smart Home Detection System — API REST

> Documentação de arquitetura e status do módulo de registro/autenticação de dispositivos.
> Este documento reflete as decisões tomadas até o momento e serve como referência para continuidade do desenvolvimento. Deve ser mantido atualizado conforme o código evolui — o código-fonte é sempre a fonte de verdade final.

## Sumário

- [Visão Geral](#visão-geral)
- [Stack Tecnológica](#stack-tecnológica)
- [Arquitetura do Módulo de Dispositivos](#arquitetura-do-módulo-de-dispositivos)
- [Convenções e Padrões de Código](#convenções-e-padrões-de-código)
- [Estrutura de Pacotes (Package-by-Feature)](#estrutura-de-pacotes-package-by-feature)
- [Pendências e Próximos Passos](#pendências-e-próximos-passos)

---

## Visão Geral

API REST em Java/Spring Boot para um sistema de detecção smart home, cobrindo registro, autenticação e gerenciamento de dispositivos. O projeto segue princípios de arquitetura limpa e separação clara entre:

- **Fluxos administrativos** (CRUD de dispositivos, gerenciados por um admin autenticado)
- **Fluxos do próprio dispositivo** (autenticação do dispositivo junto à API)

## Stack Tecnológica

- **Java** + **Spring Boot**
- **Spring Security** (autenticação baseada em JWT — a ser configurada do zero)
- **Lombok** (`@AllArgsConstructor`, `@RequiredArgsConstructor`, `@EqualsAndHashCode`)
- Padrões Spring: `@Component`, `@PreAuthorize`, controllers REST versionados

## Arquitetura do Módulo de Dispositivos

### Separação de Serviços

A lógica de dispositivos é dividida em dois componentes Spring distintos, evitando misturar responsabilidades de CRUD com responsabilidades de autenticação:

| Serviço | Responsabilidade |
|---|---|
| `DeviceService` | CRUD de dispositivos |
| `DeviceAuthService` | Autenticação e gerenciamento de chaves de acesso |

### Geração da Chave de Acesso

- A `accessKey` é **sempre gerada no servidor**, nunca aceita do cliente.
- Gerada por `DeviceKeyGenerator`, usando `SecureRandom` + codificação Base64.

### Fluxo de Autenticação

`DeviceAuthService` expõe dois métodos com propósitos diferentes:

- **`authenticate()`** — retorna `DeviceAuthResponse`, contendo o enum `DeviceMensage` (`AUTORIZADO` / `NEGADO`). Usado pelo endpoint público de autenticação do dispositivo.
- **`requireValidAccessKey()`** — lança `DeviceAuthenticationException`. Usado como guarda interna em outros fluxos que exigem uma chave válida, sem precisar montar uma resposta HTTP.

`lastCommunication` do dispositivo só é atualizado durante uma autenticação **bem-sucedida** — fluxos de atualização administrativa (`updateDevice`) não devem tocar nesse campo.

### Autorização

`@PreAuthorize("hasRole('ADMIN')")` aplicado em:

- `updateDevice`
- `deleteDevice`
- `rotateAccessKey`

> ⚠️ Ainda **não decidido**: se `createDevice` também deve exigir autorização de admin.

### Controllers

| Controller | Base path |
|---|---|
| `DeviceAuthController` | `/api/v1/device` |
| `DeviceController` | `/device` |

> ⚠️ Os base paths estão **inconsistentes** entre os dois controllers e precisam ser reconciliados (ver [Pendências](#pendências-e-próximos-passos)).

## Convenções e Padrões de Código

- **`DeviceMensage`** é um enum que pertence à **camada de autenticação** como tipo de retorno — não deve ser um campo persistido na entidade `Device`.
- **Entidade base `Entidade`**: usa `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`, com `@EqualsAndHashCode.Include` apenas no campo `uuid`. Subclasses usam `@EqualsAndHashCode(callSuper = true)` para herdar esse comportamento.
- **`DeviceMapper`** é um `@Component` (não uma classe estática) que faz o mapeamento via setters, em vez de métodos estáticos nos DTOs.


## Pendências e Próximos Passos

- [ ] **Spring Security + JWT** — implementação do zero, ainda não iniciada
- [ ] **Decisão pendente**: exigir `@PreAuthorize` de admin em `createDevice`?
- [ ] **Limpeza da estrutura de pacotes** conforme itens listados acima
- [ ] **Criação do fluxo de menssageria para dispositivos de diferentes tipos
- [ ] **Classes de definição de escolha de comunicação
- [ ] **Classes de escolha de tipologia de cameras.
---

*Última atualização: gerada a partir do histórico de decisões técnicas do projeto até o momento. Revisar e ajustar conforme o desenvolvimento avança.*
