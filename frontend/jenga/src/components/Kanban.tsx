import { Card, CardContent, CardHeader, List, ListItem, ListItemButton, ListItemText, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@suid/material"
import { useContext, createMemo, For } from "solid-js"
import { TicketResponseDTO, TicketStatus } from "../api"
import { ProjectContext } from "../provider/ProjectProvider"
import { I18nContext } from "../provider/I18nProvider"

interface KanbanItemProps {
    ticket: TicketResponseDTO
}

const KanbanItem = (props: KanbanItemProps) => {
    const pCtx = useContext(ProjectContext)
    const labels = () => (props.ticket.labels ?? []).join(", ")

    return (
        <ListItem
            sx={{
                "border": "1px solid black",
                "transform": "translateZ(0)",
            }}
            draggable
            onDragStart={(event) => {
                const id = props.ticket.id
                if (id == null) return
                event.dataTransfer?.setData("text/plain", String(id))
                pCtx?.setSelectedTicket(() => props.ticket)
            }}
        >
            <ListItemButton
                onClick={() => pCtx?.setSelectedTicket(props.ticket)}
                selected={pCtx?.selectedTicket()?.id === props.ticket.id}
            >
                <ListItemText
                    primary={props.ticket.title}
                    secondary={labels() ? `tags: ${labels()}` : undefined}
                />
            </ListItemButton>
        </ListItem>
    )
}


interface KanbanCellProps {
    tickets?: TicketResponseDTO[]
    status: TicketStatus
    username: string
}

const StatusCell = (props: KanbanCellProps) => {
    const pCtx = useContext(ProjectContext)

    return (
        <TableCell
            sx={{ "border": "1px solid black", "vertical-align": "top" }}
            onDragOver={(event) => event.preventDefault()}
            onDrop={async (event) => {
                event.preventDefault()
                const raw = event.dataTransfer?.getData("text/plain")
                const ticketId = raw ? Number(raw) : NaN
                if (Number.isNaN(ticketId)) return

                const ticket = pCtx?.tickets()?.find((item) => item.id === ticketId)
                if (!ticket) return

                const statusUnchanged = ticket.status === props.status
                const assigneeUnchanged = ticket.assignee === props.username
                if (statusUnchanged && assigneeUnchanged) return

                const projectId = pCtx?.selectedProject()?.identifier
                if (!projectId) return

                const updated: TicketResponseDTO = { ...ticket, status: props.status, assignee: props.username }
                if (!pCtx?.updateTicket) return

                try {
                    await pCtx.updateTicket(projectId, updated)
                } catch (error) {
                    console.error("Failed to update ticket from kanban drop", error)
                }
            }}
        >
            <List
                sx={{
                    "maxHeight": "30vh",
                    "overflowY": "auto",
                    "paddingTop": 0,
                    "paddingBottom": 0,
                }}
            >
                <For each={props.tickets?.filter(t => t.status === props.status)}>
                    {(ticket) => (
                        <KanbanItem ticket={ticket} />
                    )}
                </For>
            </List>
        </TableCell>
    )
}

interface RowProps {
    dev: string
    tickets?: TicketResponseDTO[]
}

const Row = (props: RowProps) => {
    return (
        <TableRow>
            <TableCell>{props.dev}</TableCell>
            <StatusCell tickets={props.tickets} status={TicketStatus.OPEN} username={props.dev} />
            <StatusCell tickets={props.tickets} status={TicketStatus.IN_PROGRESS} username={props.dev} />
            <StatusCell tickets={props.tickets} status={TicketStatus.IN_REVIEW} username={props.dev} />
            <StatusCell tickets={props.tickets} status={TicketStatus.RESOLVED} username={props.dev} />
            <StatusCell tickets={props.tickets} status={TicketStatus.CLOSED} username={props.dev} />
        </TableRow>
    )
}


interface KanbanProps {
    tickets?: TicketResponseDTO[]
}

export const Kanban = (props: KanbanProps) => {

    const pCtx = useContext(ProjectContext)
    const i18n = useContext(I18nContext)

    const tickets = createMemo(() =>
        Map.groupBy(props.tickets ?? pCtx?.tickets() ?? [], t => t.assignee ?? "")
    )

    return (
        <Card id="guide-kanban">
            <CardHeader title={i18n?.t("kanban.title")}></CardHeader>
            <CardContent>
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>{i18n?.t("kanban.dev")}</TableCell>
                                <TableCell>{i18n?.t("ticketStatus.OPEN")}</TableCell>
                                <TableCell>{i18n?.t("ticketStatus.IN_PROGRESS")}</TableCell>
                                <TableCell>{i18n?.t("ticketStatus.IN_REVIEW")}</TableCell>
                                <TableCell>{i18n?.t("ticketStatus.RESOLVED")}</TableCell>
                                <TableCell>{i18n?.t("ticketStatus.CLOSED")}</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            <For each={[...tickets().entries()]}>
                                {
                                    t => <Row dev={t[0]} tickets={t[1]}></Row>
                                }
                            </For>
                        </TableBody>
                    </Table>
                </TableContainer>
            </CardContent>
        </Card>
    )
}
