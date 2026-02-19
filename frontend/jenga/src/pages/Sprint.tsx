import { Alert, Button, Card, CardContent, CardHeader, Stack } from "@suid/material"
import { Backlog } from "../components/Backlog"
import { Kanban } from "../components/Kanban"
import { Show, createSignal, useContext } from "solid-js"
import { ProjectContext } from "../provider/ProjectProvider"
import { TicketInfo } from "../components/TicketInfo"
import { InfoMode } from "../utils/utils"


export const Sprint = () => {

    const pCtx = useContext(ProjectContext)
    const formId = "selected-ticket-form"
    const [saveError, setSaveError] = createSignal("")

    return (
        <Card sx={{}}>
            <CardHeader title="Sprint"></CardHeader>
            <CardContent>
                <Stack spacing={2}>
                    <Kanban></Kanban>
                    <Backlog></Backlog>
                </Stack>
            </CardContent>
            <Show when={pCtx?.selectedTicket()}>
                {(ticket) => (
                    <>
                        <TicketInfo
                            mode={InfoMode.Edit}
                            formId={formId}
                            ticket={ticket()}
                            onTicketChange={(next) => pCtx?.setSelectedTicket(() => next)}
                            onSubmit={async (next) => {
                                setSaveError("")
                                const projectId = pCtx?.selectedProject()?.identifier
                                if (!projectId) {
                                    setSaveError("No project selected")
                                    return
                                }

                                if (!pCtx?.updateTicket) return

                                try {
                                    await pCtx.updateTicket(projectId, next)
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
                    </>
                )}
            </Show>
        </Card>
    )
}
