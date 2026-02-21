import { Box, List, ListItem, ListItemButton, ListItemText, Popper, TextField } from "@suid/material"
import { createEffect, createMemo, createResource, createSignal, For, Show } from "solid-js"
import { UserResourceService } from "../api"

interface SearchUserProps {
    setSelected: (username: string) => void
    selected: string
    disabled?: boolean
    label?: string
}

export const SearchUser = (props: SearchUserProps) => {
    let ref: HTMLDivElement | undefined

    const [input, setInput] = createSignal(props.selected)
    const [options] = createResource(input, async q => await UserResourceService.getApiUsersSearch(q))
    const [focused, setFocused] = createSignal(false)
    const hasSearchError = createMemo(() => Boolean(options.error))

    createEffect(() => {
        setInput(props.selected)
    })

    const open = createMemo(() => {
        if (!focused()) return false
        if (props.disabled) return false
        if (hasSearchError() || !options()) return false
        return options()!.length > 0
    })

    return (
        <>
            <div ref={el => ref = el}>
                <TextField
                    label={props.label}
                    value={input()}
                    onChange={(event) => {
                        const value = event.currentTarget.value
                        setInput(value)
                        props.setSelected(value)
                    }}
                    onFocus={() => setFocused(true)}
                    onBlur={() => setFocused(false)}
                    disabled={props.disabled}
                    error={hasSearchError()}
                    helperText={hasSearchError() ? "Failed to load users" : undefined}
                    fullWidth
                />
            </div>
            <Popper anchorEl={ref} open={open()} placement="bottom-start" style={{ "z-index": 1 }}>
                <Box backgroundColor="white">
                        <Show when={!hasSearchError() && options()}>
                            {
                                <List dense>
                                    <For each={options()}>
                                        {
                                            u =>
                                                <ListItem>
                                                    <ListItemButton
                                                        onMouseDown={(event) => {
                                                            event.preventDefault()
                                                            const username = u?.username ?? ""
                                                            setInput(username)
                                                            props.setSelected(username)
                                                        }}
                                                    >
                                                        <ListItemText primary={u.username} secondary={u.email}></ListItemText>
                                                    </ListItemButton>
                                                </ListItem>
                                        }
                                    </For>
                                </List>
                            }
                        </Show>
                </Box>
            </Popper>
        </>
    )
}
