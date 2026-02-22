import { FormControl, InputLabel, MenuItem, Select, Stack, TextField } from "@suid/material"
import { For, Setter, useContext } from "solid-js"
import { TicketResponseDTO, TicketPriority, TicketSize, TicketStatus } from "../api"
import { InfoMode } from "../utils/utils"
import { SearchUser } from "./SearchUser"
import { LabelSelector } from "./Labels"
import { I18nContext } from "../provider/I18nProvider"
interface TicketInfoProps {
    mode: InfoMode
    ticket: TicketResponseDTO
    onTicketChange: (next: TicketResponseDTO) => void
    onSubmit?: (next: TicketResponseDTO) => void
    formId?: string
}

export const TicketInfo = (props: TicketInfoProps) => {
    const formId = props.formId ?? "ticket-info-form"
    const i18n = useContext(I18nContext)

    const isReadOnly = props.mode === InfoMode.ReadOnly

    const updateTicket = <K extends keyof TicketResponseDTO>(key: K, value: TicketResponseDTO[K]) => {
        const updatedTicket = { ...props.ticket, [key]: value }
        props.onTicketChange(updatedTicket)
    }

    const setLabels: Setter<string[]> = (next) => {
        const current = props.ticket.labels ?? []
        const labels = typeof next === "function" ? next(current) : next
        updateTicket("labels", labels)
        return labels
    }

    return (
        <form
            id={formId}
            onSubmit={(event) => {
                event.preventDefault()
                props.onSubmit?.({ ...props.ticket })
            }}
        >
            <Stack spacing={1.5}>
                <TextField
                    name="title"
                    label={i18n?.t("ticketInfo.title")}
                    value={props.ticket.title ?? ""}
                    onChange={(_, value) => updateTicket("title", value)}
                    required
                    disabled={isReadOnly}
                />
                <TextField
                    name="description"
                    label={i18n?.t("ticketInfo.description")}
                    value={props.ticket.description ?? ""}
                    onChange={(_, value) => updateTicket("description", value)}
                    rows={5}
                    multiline
                    disabled={isReadOnly}
                />
                <SearchUser
                    selected={props.ticket.assignee ?? ""}
                    setSelected={(username) => updateTicket("assignee", username)}
                    disabled={isReadOnly}
                    label={i18n?.t("ticketInfo.assignee")}
                />
                <LabelSelector
                    selected={() => props.ticket.labels ?? []}
                    setSelected={setLabels}
                    disabled={isReadOnly}
                />
                <FormControl fullWidth>
                    <InputLabel id={`${formId}-priority-label`}>{i18n?.t("ticketInfo.priority")}</InputLabel>
                    <Select
                        labelId={`${formId}-priority-label`}
                        value={props.ticket.priority ?? TicketPriority.MEDIUM}
                        label={i18n?.t("ticketInfo.priority")}
                        onChange={(event) => updateTicket("priority", event.target.value as TicketPriority)}
                        disabled={isReadOnly}
                    >
                        <For each={Object.values(TicketPriority)}>
                            {(priority) => (
                                <MenuItem value={priority}>{i18n?.t(`ticketPriority.${priority}`)}</MenuItem>
                            )}
                        </For>
                    </Select>
                </FormControl>
                <FormControl fullWidth>
                    <InputLabel id={`${formId}-size-label`}>{i18n?.t("ticketInfo.size")}</InputLabel>
                    <Select
                        labelId={`${formId}-size-label`}
                        value={props.ticket.size ?? TicketSize.MEDIUM}
                        label={i18n?.t("ticketInfo.size")}
                        onChange={(event) => updateTicket("size", event.target.value as TicketSize)}
                        disabled={isReadOnly}
                    >
                        <For each={Object.values(TicketSize)}>
                            {(size) => (
                                <MenuItem value={size}>{i18n?.t(`ticketSize.${size}`)}</MenuItem>
                            )}
                        </For>
                    </Select>
                </FormControl>
                <FormControl fullWidth>
                    <InputLabel id={`${formId}-status-label`}>{i18n?.t("ticketInfo.status")}</InputLabel>
                    <Select
                        labelId={`${formId}-status-label`}
                        value={props.ticket.status ?? TicketStatus.OPEN}
                        label={i18n?.t("ticketInfo.status")}
                        onChange={(event) => updateTicket("status", event.target.value as TicketStatus)}
                        disabled={isReadOnly}
                    >
                        <For each={Object.values(TicketStatus)}>
                            {(status) => (
                                <MenuItem value={status}>{i18n?.t(`ticketStatus.${status}`)}</MenuItem>
                            )}
                        </For>
                    </Select>
                </FormControl>
            </Stack>
        </form>
    )
}
