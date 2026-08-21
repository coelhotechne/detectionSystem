# Smart Home Detection System — API REST

## Módulo de Registro/Autenticação de Dispositivos

> Documentação de arquitetura e status do módulo de registro/autenticação de dispositivos. Este documento reflete as decisões tomadas até o momento e serve como referência para continuidade do desenvolvimento. Deve ser mantido atualizado conforme o código evolui — **o código-fonte é sempre a fonte de verdade final**.

## Sumário

- [Visão Geral](#visão-geral)
- [Stack Tecnológica](#stack-tecnológica)
- [Arquitetura do Módulo de Dispositivos](#arquitetura-do-módulo-de-dispositivos)
- [Convenções e Padrões de Código](#convenções-e-padrões-de-código)
- [Estrutura de Pacotes (Package-by-Feature)](#estrutura-de-pacotes-package-by-feature)
- [Pendências e Próximos Passos](#pendências-e-próximos-passos)

## Visão Geral

API REST em Java/Spring Boot para um sistema de detecção smart home, cobrindo registro, autenticação e gerenciamento de dispositivos. O projeto segue princípios de arquitetura limpa e separação clara entre:

- **Fluxos administrativos** (CRUD de dispositivos, gerenciados por um admin autenticado)
- **Fluxos do próprio dispositivo** (autenticação do dispositivo junto à API)

**Papel do Device no pipeline geral de detecção:** no domínio mais amplo do sistema, `Device` (smartwatch, TV, tablet, celular) é exclusivamente **alvo de notificação**, não fonte de detecção — quem produz evento é `Sensor`/`Cam`. Um `Device` se registra e se autentica na rede apenas para *receber* atualizações do sistema. Essa separação de papel é o que justifica o módulo ser tratado isoladamente dos módulos de detecção (`sensor`, `cam`, `event`). Ver [Relação com o Pipeline de Detecção](#relação-com-o-pipeline-de-detecção).

## Stack Tecnológica

- Java + Spring Boot
- Spring Security (autenticação baseada em JWT — a ser configurada do zero)
- Lombok (`@AllArgsConstructor`, `@RequiredArgsConstructor`, `@EqualsAndHashCode`)
- Padrões Spring: `@Component`, `@PreAuthorize`, controllers REST versionados

## Arquitetura do Módulo de Dispositivos

### Separação de Serviços

A lógica de dispositivos é dividida em dois componentes Spring distintos, evitando misturar responsabilidades de CRUD com responsabilidades de autenticação:

| Serviço | Responsabilidade |
|---|---|
| `DeviceService` | CRUD de dispositivos |
| `DeviceAuthService` | Autenticação e gerenciamento de chaves de acesso |

### Geração da Chave de Acesso

- A `accessKey` é sempre gerada no servidor, nunca aceita do cliente.
- Gerada por `DeviceKeyGenerator`, usando `SecureRandom` + codificação Base64.

### Fluxo de Autenticação

`DeviceAuthService` expõe dois métodos com propósitos diferentes:

- **`authenticate()`** — retorna `DeviceAuthResponse`, contendo o enum `DeviceMensage` (`AUTORIZADO` / `NEGADO`). Usado pelo endpoint público de autenticação do dispositivo.
- **`requireValidAccessKey()`** — lança `DeviceAuthenticationException`. Usado como guarda interna em outros fluxos que exigem uma chave válida, sem precisar montar uma resposta HTTP.

`lastCommunication` do dispositivo só é atualizado durante uma autenticação bem-sucedida — fluxos de atualização administrativa (`updateDevice`) não devem tocar nesse campo.

> **Nota (optimistic locking):** se `Device` estender a entidade base do projeto, `rotateAccessKey` e `updateDevice` concorrentes na mesma linha devem ser protegidos por `@Version` — mesmo padrão adotado no módulo `sensor`. Confirmar se já está presente antes de considerar esse fluxo concluído.

### Autorização

`@PreAuthorize("hasRole('ADMIN')")` aplicado em:

- `updateDevice`
- `deleteDevice`
- `rotateAccessKey`

⚠️ **Ainda não decidido:** se `createDevice` também deve exigir autorização de admin.

### Controllers

| Controller | Base path |
|---|---|
| `DeviceAuthController` | `/api/v1/device` |
| `DeviceController` | `/device` |

⚠️ Os base paths estão inconsistentes entre os dois controllers e precisam ser reconciliados (ver [Pendências](#pendências-e-próximos-passos)).

### Relação com o Pipeline de Detecção

- `Device` **não** implementa o mesmo contrato de `Sensor`/`Cam` — ele não é uma origem de evento de detecção, é o destino da notificação.
- A entrega de notificações é modelada por uma entidade associativa (`Event` × `Device`), com status próprio de entrega (`PENDING`, `SENT`, `DELIVERED`, `READ`, `FAILED`) — permite que um evento notifique múltiplos devices, cada um com seu próprio rastro de entrega.
- Caso um dispositivo físico também venha a **produzir** dado de detecção no futuro (ex.: acelerômetro de smartwatch alimentando detecção de movimento), o modelo previsto **não** é fundir `Device` com `Sensor`. A entidade `Sensor` ganharia uma referência opcional de volta (`hostDevice`), mantendo `Device` com responsabilidade única de alvo de notificação.

## Convenções e Padrões de Código

- `DeviceMensage` é um enum que pertence à camada de autenticação como tipo de retorno — não deve ser um campo persistido na entidade `Device`.
- `DeviceMapper` é um `@Component` (não uma classe estática) que faz o mapeamento via setters, em vez de métodos estáticos nos DTOs.

**Entidade base:**

- Usa `@Getter` (não `@Data`) — não devem existir setters públicos para `uuid`, `version`, `createdBy`, `lastModifiedBy`, `createdAt`, `updatedAt`. Esses campos são geridos exclusivamente pelo Hibernate (`uuid`, `version`) e pelo `AuditingEntityListener` (auditoria), via acesso direto a field — nenhum setter é necessário para esse funcionamento.
- `@Version` presente para optimistic locking, com `@EqualsAndHashCode.Exclude` (campo mutável não deve afetar identidade do objeto).
- `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`, com `@EqualsAndHashCode.Include` apenas no campo `uuid` — identidade da entidade é exclusivamente o ID.
- **Subclasses devem usar `@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)`** — a flag `onlyExplicitlyIncluded` **não é herdada automaticamente** via `callSuper`. Omiti-la faz o Lombok incluir todos os campos próprios da subclasse na comparação (inclusive relacionamentos `@ManyToOne` lazy), reabrindo o problema de hashCode instável e risco de `LazyInitializationException` que essa convenção existe pra evitar.

⚠️ **Verificar nomenclatura:** este documento se refere à superclasse como "Entidade"; confirmar se corresponde à mesma classe `BaseEntity` usada nos demais módulos do projeto (sensor, location, zone), para não acabar com duas classes-base divergentes.

**Exceptions:** seguir o mesmo padrão adotado no módulo `sensor` — subclasses de `ErrorResponseException`, com `ProblemDetail` estruturado (`title` + propriedades customizadas) em vez de mensagem de texto solta no corpo. `DeviceAuthenticationException` deve seguir essa convenção, se ainda não seguir.

## Estrutura de Pacotes (Package-by-Feature)

Mesma convenção adotada nos demais módulos do projeto (`sensor`, `location`):

```
device/
├── api/
│   ├── controller/
│   └── dto/
├── application/
├── domain/
├── exceptions/
└── infrastructure/
```

⚠️ Ao criar o pacote `exceptions`, atenção ao nome — o módulo `sensor` tem esse pacote grafado como `excpetions` (typo). Não replicar o erro no módulo `device`.

## Pendências e Próximos Passos

- [ ] Spring Security + JWT — implementação do zero, ainda não iniciada
- [ ] Decisão pendente: exigir `@PreAuthorize` de admin em `createDevice`?
- [ ] Reconciliar base paths inconsistentes entre `DeviceAuthController` (`/api/v1/device`) e `DeviceController` (`/device`)
- [ ] Confirmar se "Entidade" (citada nas convenções) é a mesma classe `BaseEntity` usada no restante do projeto
- [ ] Aplicar `onlyExplicitlyIncluded = true` nas subclasses de entidade (`Sensor`, `Zone`, `Location`, `Device`) — bug identificado: a exclusão de campos do `equals`/`hashCode` não estava se propagando via `callSuper`
- [ ] Confirmar presença de `@Version` na entidade `Device` (optimistic locking para `rotateAccessKey`/`updateDevice` concorrentes)
- [ ] Corrigir typo de pacote `excpetions` → `exceptions` no módulo `sensor`
- [ ] Modelar entidade de entrega de notificação (`Event` × `Device`) para o fluxo de notificação
- [ ] Criação do fluxo de mensageria para dispositivos de diferentes tipos
- [ ] Classes de definição de escolha de comunicação
- [ ] Classes de escolha de tipologia de câmeras

---

*Última atualização: consolidada a partir do histórico de decisões técnicas do projeto, incluindo correções na entidade base (BaseEntity/Entidade), modelagem de Sensor/Zone/Location e definição do papel de Device no pipeline de detecção. Revisar e ajustar conforme o desenvolvimento avança.*
