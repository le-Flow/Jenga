import { Alert, Dialog, DialogTitle, DialogContent, DialogActions, Button } from "@suid/material"
import { Setter, useContext, createSignal, createEffect, Show } from "solid-js"
import { TicketPriority, TicketSize, TicketStatus, TicketRequestDTO, TicketResponseDTO, TicketResourceService } from "../api"
import { ProjectContext } from "../provider/ProjectProvider"
import { TicketInfo } from "./TicketInfo"
import { InfoMode } from "../utils/utils"

interface NewTicketDialogProps {
    open: boolean
    setOpen: Setter<boolean>
}

export const NewTicketDialog = (props: NewTicketDialogProps) => {
    const pCtx = useContext(ProjectContext)

    const EMPTY_TICKET: TicketResponseDTO = {
        title: "",
        description: "",
        priority: TicketPriority.MEDIUM,
        size: TicketSize.MEDIUM,
        status: TicketStatus.OPEN,
        assignee: "",
    }

    const formId = "new-ticket-form"
    const [ticket, setTicket] = createSignal<TicketResponseDTO>({ ...EMPTY_TICKET })
    const [createError, setCreateError] = createSignal("")

    createEffect(() => {
        if (props.open) {
            setTicket(() => ({
                ...EMPTY_TICKET,
                projectName: pCtx?.selectedProject().name,
            }))
            setCreateError("")
        }
    })

    const onCreate = async (draft?: TicketResponseDTO) => {
        const source = draft ?? ticket()
        const project = pCtx?.selectedProject()
        if (!project?.identifier) {
            setCreateError("No project selected")
            return
        }

        const request: TicketRequestDTO = {
            projectName: project.name,
            title: source.title ?? "",
            description: source.description ?? "",
            priority: source.priority ?? TicketPriority.MEDIUM,
            size: source.size ?? TicketSize.MEDIUM,
            status: source.status ?? TicketStatus.OPEN,
            assignee: source.assignee ?? "",
        }

        setCreateError("")

        try {
            const newTicket = await TicketResourceService.postApiTickets(project.identifier, request)
            pCtx?.setTickets(prev => [...prev ?? [], newTicket])
            props.setOpen(false)
        } catch (error) {
            console.error("Failed to create ticket", error)
            setCreateError("Failed to create ticket")
        }
    }

    return (
        <Dialog open={props.open} fullWidth>
            <DialogTitle title="New Ticket">New Ticket</DialogTitle>
            <DialogContent>
                <Show when={createError()}>
                    {(message) => <Alert severity="error">{message()}</Alert>}
                </Show>
                <TicketInfo
                    mode={InfoMode.Create}
                    formId={formId}
                    ticket={ticket()}
                    onTicketChange={setTicket}
                    onSubmit={onCreate}
                />
            </DialogContent>
            <DialogActions>
                <Button type="button" onClick={() => { props.setOpen(false) }}>cancel</Button>
                <Button type="submit" form={formId}>create</Button>
            </DialogActions>
        </Dialog>
    )
}
