import { Alert, Box, Button, Card, CardContent, CardHeader, FormControl, InputLabel, MenuItem, Select, Stack, TextField, Typography } from "@suid/material"
import { CheckCircle } from "@suid/icons-material"
import { Backlog } from "../components/Backlog"
import { Kanban } from "../components/Kanban"
import { Show, createMemo, createSignal, useContext } from "solid-js"
import { ProjectContext } from "../provider/ProjectProvider"
import { TicketInfo } from "../components/TicketInfo"
import { InfoMode } from "../utils/utils"
import { TicketResponseDTO } from "../api"
import "./Sprint.css"

type TicketFilterField = "all" | "title" | "description" | "assignee" | "reporter" | "status" | "labels"

export const Sprint = () => {

    const pCtx = useContext(ProjectContext)
    const formId = "selected-ticket-form"
    const [saveError, setSaveError] = createSignal("")
    const [saveSuccess, setSaveSuccess] = createSignal(false)
    const [filterBy, setFilterBy] = createSignal<TicketFilterField>("all")
    const [filterValue, setFilterValue] = createSignal("")

    const includesFilter = (value?: string) => value?.toLowerCase().includes(filterValue().trim().toLowerCase()) ?? false
    const includesLabelFilter = (labels?: string[]) =>
        labels?.some((label) => label.toLowerCase().includes(filterValue().trim().toLowerCase())) ?? false

    const matchesTicket = (ticket: TicketResponseDTO) => {
        if (!filterValue().trim()) return true

        switch (filterBy()) {
            case "title":
                return includesFilter(ticket.title)
            case "description":
                return includesFilter(ticket.description)
            case "assignee":
                return includesFilter(ticket.assignee)
            case "reporter":
                return includesFilter(ticket.reporter)
            case "status":
                return includesFilter(ticket.status)
            case "labels":
                return includesLabelFilter(ticket.labels)
            case "all":
            default:
                return (
                    includesFilter(ticket.title)
                    || includesFilter(ticket.description)
                    || includesFilter(ticket.assignee)
                    || includesFilter(ticket.reporter)
                    || includesFilter(ticket.status)
                    || includesLabelFilter(ticket.labels)
                )
        }
    }

    const filteredTickets = createMemo(() => (pCtx?.tickets() ?? []).filter(matchesTicket))

    return (
        <Card>
            <CardHeader title="Sprint"></CardHeader>
            <CardContent>
                <Box class="sprint-layout">
                    <Stack spacing={2}>
                        <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5}>
                            <FormControl sx={{ "minWidth": "12rem" }}>
                                <InputLabel id="ticket-filter-by-label">Filter by</InputLabel>
                                <Select
                                    labelId="ticket-filter-by-label"
                                    value={filterBy()}
                                    label="Filter by"
                                    onChange={(event) => setFilterBy(event.target.value as TicketFilterField)}
                                >
                                    <MenuItem value="all">All fields</MenuItem>
                                    <MenuItem value="title">Title</MenuItem>
                                    <MenuItem value="description">Description</MenuItem>
                                    <MenuItem value="assignee">Assignee</MenuItem>
                                    <MenuItem value="reporter">Reporter</MenuItem>
                                    <MenuItem value="status">Status</MenuItem>
                                    <MenuItem value="labels">Labels</MenuItem>
                                </Select>
                            </FormControl>
                            <TextField
                                fullWidth
                                label="Ticket filter"
                                placeholder="Type to filter tickets..."
                                value={filterValue()}
                                onChange={(_, value) => setFilterValue(value)}
                            />
                        </Stack>
                        <Kanban tickets={filteredTickets()}></Kanban>
                        <Backlog tickets={filteredTickets()}></Backlog>
                    </Stack>

                    <Card variant="outlined" class="ticket-sidebar">
                        <CardHeader title="Ticket Details" />
                        <CardContent>
                            <Show
                                when={pCtx?.selectedTicket()}
                                fallback={
                                    <Typography>
                                        Please select a ticket
                                    </Typography>
                                }
                            >
                                {(ticket) => (
                                    <Stack spacing={1.5}>
                                        <TicketInfo
                                            mode={InfoMode.Edit}
                                            formId={formId}
                                            ticket={ticket()}
                                            onTicketChange={(next) => {
                                                setSaveSuccess(false)
                                                setSaveError("")
                                                pCtx?.setSelectedTicket(() => next)
                                            }}
                                            onSubmit={async (next) => {
                                                setSaveError("")
                                                setSaveSuccess(false)
                                                const projectId = pCtx?.selectedProject()?.identifier
                                                if (!projectId) {
                                                    setSaveError("No project selected")
                                                    return
                                                }

                                                if (!pCtx?.updateTicket) return

                                                try {
                                                    await pCtx.updateTicket(projectId, next)
                                                    setSaveSuccess(true)
                                                } catch (error) {
                                                    console.error("Failed to save ticket", error)
                                                    setSaveError("Failed to save ticket")
                                                }
                                            }}
                                        />
                                        <Button type="submit" form={formId}>
                                            save
                                        </Button>
                                        <Show when={saveError()}>
                                            {(message) => <Alert severity="error">{message()}</Alert>}
                                        </Show>
                                        <Show when={saveSuccess()}>
                                            <Alert severity="success" icon={<CheckCircle />}>
                                                Ticket saved
                                            </Alert>
                                        </Show>
                                    </Stack>
                                )}
                            </Show>
                        </CardContent>
                    </Card>
                </Box>
            </CardContent>
        </Card>
    )
}
