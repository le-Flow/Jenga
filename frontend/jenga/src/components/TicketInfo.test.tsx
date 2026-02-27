// @vitest-environment jsdom
import { cleanup, fireEvent, render, screen, waitFor } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, test, vi } from "vitest";
import { TicketInfo } from "./TicketInfo";
import { InfoMode } from "../utils/utils";
import { TicketPriority, TicketSize, TicketStatus } from "../api";
import { withTestProviders } from "../test/testProviders";

vi.mock("./SearchUser", () => ({
    SearchUser: () => <div data-testid="search-user" />,
}));

vi.mock("./Labels", () => ({
    LabelSelector: () => <div data-testid="label-selector" />,
}));

describe("TicketInfo", () => {
    afterEach(() => cleanup());

    const baseTicket = {
        id: 42,
        title: "Template Ticket",
        description: "Template Description",
        priority: TicketPriority.MEDIUM,
        size: TicketSize.MEDIUM,
        status: TicketStatus.OPEN,
        assignee: "alice",
        labels: [],
        acceptanceCriteria: [],
    };

    test("renders key form fields template", () => {
        const { container } = render(() =>
            withTestProviders(
                <TicketInfo
                    mode={InfoMode.Edit}
                    ticket={baseTicket}
                    onTicketChange={() => { }}
                />,
            ),
        );

        expect(container.querySelector("form#ticket-info-form")).toBeInTheDocument();
        expect(screen.getByDisplayValue("Template Ticket")).toBeInTheDocument();
    });

    test("renders editable title input in edit mode", () => {
        const { container } = render(() =>
            withTestProviders(
                <TicketInfo
                    mode={InfoMode.Edit}
                    ticket={baseTicket}
                    onTicketChange={() => { }}
                />,
            ),
        );

        const titleInput = container.querySelector("input[name='title']") as HTMLInputElement;
        expect(titleInput).toBeInTheDocument();
        expect(titleInput).not.toBeDisabled();
        expect(titleInput.value).toBe("Template Ticket");
    });

    test("renders id relation inputs", () => {
        const { container } = render(() =>
            withTestProviders(
                <TicketInfo
                    mode={InfoMode.Edit}
                    ticket={baseTicket}
                    onTicketChange={() => { }}
                />,
            ),
        );

        const relatedInput = container.querySelector("input[name='relatedTickets']") as HTMLInputElement;
        const blockingInput = container.querySelector("input[name='blockingTickets']") as HTMLInputElement;
        const blockedInput = container.querySelector("input[name='blockedTickets']") as HTMLInputElement;
        expect(relatedInput).toBeInTheDocument();
        expect(blockingInput).toBeInTheDocument();
        expect(blockedInput).toBeInTheDocument();
    });

    test("calls onSubmit with normalized ticket payload", async () => {
        const user = userEvent.setup();
        const onSubmit = vi.fn();
        const { container } = render(() =>
            withTestProviders(
                <TicketInfo
                    mode={InfoMode.Edit}
                    ticket={baseTicket}
                    onTicketChange={() => { }}
                    onSubmit={onSubmit}
                />,
            ),
        );

        const relatedInput = container.querySelector("input[name='relatedTickets']") as HTMLInputElement;
        const blockingInput = container.querySelector("input[name='blockingTickets']") as HTMLInputElement;
        const blockedInput = container.querySelector("input[name='blockedTickets']") as HTMLInputElement;
        const form = container.querySelector("form#ticket-info-form") as HTMLFormElement;

        await user.clear(relatedInput);
        await user.type(relatedInput, "1,2,2");
        await user.clear(blockingInput);
        await user.type(blockingInput, "3,4");
        await user.clear(blockedInput);
        await user.type(blockedInput, "5,6,6");
        await fireEvent.submit(form);

        await waitFor(() => {
            expect(onSubmit).toHaveBeenCalledTimes(1);
            expect(onSubmit).toHaveBeenCalledWith(
                expect.objectContaining({
                    relatedTicketsIds: [1, 2],
                    blockingTicketIds: [3, 4],
                    blockedTicketIds: [5, 6],
                }),
            );
        });
    });
});
